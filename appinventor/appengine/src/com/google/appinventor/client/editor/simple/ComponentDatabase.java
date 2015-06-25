// -*- mode: java; c-basic-offset: 2; -*-
// Copyright 2009-2011 Google, All Rights reserved
// Copyright 2011-2012 MIT, All rights reserved
// Released under the Apache License, Version 2.0
// http://www.apache.org/licenses/LICENSE-2.0

package com.google.appinventor.client.editor.simple;

import com.google.appinventor.client.output.OdeLog;
import com.google.appinventor.components.common.ComponentCategory;
import com.google.appinventor.shared.properties.json.JSONArray;
import com.google.appinventor.shared.properties.json.JSONObject;
import com.google.appinventor.shared.properties.json.JSONValue;
import com.google.appinventor.shared.simple.ComponentDatabaseInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Database holding information of Simple components and their properties.
 *
 */
class ComponentDatabase implements ComponentDatabaseInterface {

  /*
   * Simple component information: component name, its properties
   */
  private static class Component {
    private final String name;
    private final int version;
    private final String type;
    private final boolean external;
    private final String categoryString;
    private final String helpString;
    private final boolean showOnPalette;
    private final String categoryDocUrlString;
    private final List<PropertyDefinition> properties;
    private final List<BlockPropertyDefinition> blockProperties;
    private final List<EventDefinition> events;
    private final List<MethodDefinition> methods;
    private final Map<String, String> propertiesTypesByName;
    private final boolean nonVisible;
    private final String iconName;
    private final String typeDescription;

    Component(String name, int version, String type, boolean external, String categoryString, String helpString,
        boolean showOnPalette, boolean nonVisible, String iconName, String typeDescription) {
      this.name = name;
      this.version = version;
      this.type = type;
      this.external = external;
      this.categoryString = categoryString;
      this.helpString = helpString;
      this.showOnPalette = showOnPalette;
      this.categoryDocUrlString = ComponentCategory.valueOf(categoryString).getDocName();
      this.properties = new ArrayList<PropertyDefinition>();
      this.blockProperties = new ArrayList<BlockPropertyDefinition>();
      this.events = new ArrayList<EventDefinition>();
      this.methods = new ArrayList<MethodDefinition>();
      this.propertiesTypesByName = new HashMap<String, String>();
      this.nonVisible = nonVisible;
      this.iconName = iconName;
      this.typeDescription = typeDescription;
    }

    void add(PropertyDefinition property) {
      properties.add(property);
      propertiesTypesByName.put(property.getName(), property.getEditorType());
    }

    void add(BlockPropertyDefinition blockProperty) {
      blockProperties.add(blockProperty);
    }

    void add(EventDefinition event) {
      events.add(event);
    }

    void add(MethodDefinition method) {
      methods.add(method);
    }
  }

  // Maps component names to component descriptors
  private Map<String, Component> components;

  
  // Components in JSON String generated from components
  private String componentsJSONString;


  /**
   * Creates a new component database.
   *
   * @param array
   *          a JSONArray of components
   */
  ComponentDatabase(JSONArray array) {
    components = new HashMap<String, Component>();
    for (JSONValue component : array.getElements()) {
      initComponent(component.asObject()); 
    }
    
    componentsJSONString = generateComponentsJSON();
  }

  public int addComponents(JSONArray array) {
    int compsAdded = 0;
    for ( JSONValue component : array.getElements()){
      if(initComponent(component.asObject())) ++compsAdded;
    }
    
    componentsJSONString = generateComponentsJSON();
    return compsAdded;
  }
  
  
  @Override
  public Set<String> getComponentNames() {
    return components.keySet();
  }

  @Override
  public int getComponentVersion(String componentTypeName) {
    Component component = components.get(componentTypeName);
    if (component == null) {
      throw new IllegalArgumentException();
    }

    return component.version;
  }
  
  @Override
  public String getComponentType(String componentTypeName){
    Component component = components.get(componentTypeName);
    if(component == null){
      throw new IllegalArgumentException();
    }
    
    return component.type;
  }
  
  @Override
  public boolean getComponentExternal(String componentTypeName){
    Component component = components.get(componentTypeName);
    if(component == null){
      throw new IllegalArgumentException();
    }
    
    return component.external;
  }
  
  @Override
  public String getCategoryString(String componentTypeName) {
    Component component = components.get(componentTypeName);
    if (component == null) {
      throw new IllegalArgumentException();
    }

    return component.categoryString;
  }

  @Override
  public String getCategoryDocUrlString(String componentTypeName) {
    Component component = components.get(componentTypeName);
    if (component == null) {
      throw new IllegalArgumentException();
    }

    return component.categoryDocUrlString;
  }

  @Override
  public String getHelpString(String componentTypeName) {
    Component component = components.get(componentTypeName);
    if (component == null) {
      throw new IllegalArgumentException();
    }

    return component.helpString;
  }

  @Override
  public boolean getShowOnPalette(String componentTypeName) {
    Component component = components.get(componentTypeName);
    if (component == null) {
      throw new IllegalArgumentException();
    }

    return component.showOnPalette;
  }

  @Override
  public boolean getNonVisible(String componentTypeName) {
    Component component = components.get(componentTypeName);
    if (component == null) {
      throw new IllegalArgumentException();
    }
    return component.nonVisible;
  }

  @Override
  public String getIconName(String componentTypeName) {
    Component component = components.get(componentTypeName);
    if (component == null) {
      throw new IllegalArgumentException();
    }
    return component.iconName;
  }

  @Override
  public List<PropertyDefinition> getPropertyDefinitions(String componentTypeName) {
    Component component = components.get(componentTypeName);
    if (component == null) {
      throw new IllegalArgumentException();
    }

    return component.properties;
  }

  @Override
  public List<BlockPropertyDefinition> getBlockPropertyDefinitions(String componentTypeName) {
    Component component = components.get(componentTypeName);
    if (component == null) {
      throw new IllegalArgumentException();
    }

    return component.blockProperties;
  }

  @Override
  public List<EventDefinition> getEventDefinitions(String componentTypeName) {
    Component component = components.get(componentTypeName);
    if (component == null) {
      throw new IllegalArgumentException();
    }

    return component.events;
  }

  @Override
  public List<MethodDefinition> getMethodDefinitions(String componentTypeName) {
    Component component = components.get(componentTypeName);
    if (component == null) {
      throw new IllegalArgumentException();
    }

    return component.methods;
  }

  @Override
  public Map<String, String> getPropertyTypesByName(String componentTypeName) {
    Component component = components.get(componentTypeName);
    if (component == null) {
      throw new IllegalArgumentException();
    }

    return component.propertiesTypesByName;
  }

  @Override
  public String getTypeDescription(String componentTypeName) {
    Component component = components.get(componentTypeName);
    if (component == null) {
      throw new IllegalArgumentException();
    }

    return component.typeDescription;
  }

  public String getComponentsJSONString() {
    return componentsJSONString;
  }

  /*
   * Creates a component descriptor from the contents of the JSON file and puts
   * it in the components map.
   */
  private boolean initComponent(JSONObject componentNode) {
    Map<String, JSONValue> properties = componentNode.getProperties();
    String name = properties.get("name").asString().getString();
    if(components.containsKey(name))  return false;
    Component component = new Component(name,
        Integer.parseInt(properties.get("version").asString().getString()), 
        properties.get("classpath").asString().getString(), 
        Boolean.valueOf(properties.get("external").asString().getString()),
        properties.get("categoryString").asString().getString(), 
        properties.get("helpString").asString().getString(), 
        Boolean.valueOf(properties.get("showOnPalette").asString().getString()),
        Boolean.valueOf(properties.get("nonVisible").asString().getString()), 
        properties.get("iconName").asString().getString(), componentNode.toJson());
    findComponentProperties(component, properties.get("properties").asArray());
    findComponentBlockProperties(component, properties.get("blockProperties").asArray());
    findComponentEvents(component, properties.get("events").asArray());
    findComponentMethods(component, properties.get("methods").asArray());
    components.put(component.name, component);
    return true;
  }

  /*
   * Enters property information into the component descriptor.
   */
  private void findComponentProperties(Component component, JSONArray propertiesArray) {
    for (JSONValue propertyValue : propertiesArray.getElements()) {
      Map<String, JSONValue> properties = propertyValue.asObject().getProperties();
      component.add(new PropertyDefinition(properties.get("name").asString().getString(),
          properties.get("defaultValue").asString().getString(), properties.get("editorType")
              .asString().getString()));
    }
  }

  /*
   * Enters block property information into the component descriptor.
   */
  private void findComponentBlockProperties(Component component, JSONArray blockPropertiesArray) {
    for (JSONValue blockPropertyValue : blockPropertiesArray.getElements()) {
      Map<String, JSONValue> blockProperties = blockPropertyValue.asObject().getProperties();
      component.add(new BlockPropertyDefinition(blockProperties.get("name").asString().getString(),
          blockProperties.get("description").asString().getString(), blockProperties.get("type")
              .asString().getString(), blockProperties.get("rw").asString().getString()));
    }
  }

  /*
   * Enters event information into the component descriptor.
   */
  private void findComponentEvents(Component component, JSONArray eventsArray) {
    for (JSONValue eventValue : eventsArray.getElements()) {
      Map<String, JSONValue> event = eventValue.asObject().getProperties();

      List<ParameterDefinition> paramList = new ArrayList<ParameterDefinition>();
      List<JSONValue> params = event.get("params").asArray().getElements();
      for (int i = 0; i < params.size(); ++i) {
        JSONValue paramValue = params.get(i);
        Map<String, JSONValue> param = paramValue.asObject().getProperties();
        paramList.add(new ParameterDefinition(param.get("name").asString().getString(), param
            .get("type").asString().getString()));
      }
      component.add(
          new EventDefinition(
              event.get("name").asString().getString(),
              event.get("description").asString().getString(),
              new Boolean(event.get("deprecated").asString().getString()),
              paramList));
    }
  }

  /*
   * Enters method information into the component descriptor.
   */
  private void findComponentMethods(Component component, JSONArray methodsArray) {
    for (JSONValue blockPropertyValue : methodsArray.getElements()) {
      Map<String, JSONValue> method = blockPropertyValue.asObject().getProperties();

      List<ParameterDefinition> paramList = new ArrayList<ParameterDefinition>();
      List<JSONValue> params = method.get("params").asArray().getElements();
      for (int i = 0; i < params.size(); ++i) {
        JSONValue paramValue = params.get(i);
        Map<String, JSONValue> param = paramValue.asObject().getProperties();
        paramList.add(new ParameterDefinition(param.get("name").asString().getString(), param
            .get("type").asString().getString()));
      }
      component.add(
          new MethodDefinition(
              method.get("name").asString().getString(),
              method.get("description").asString().getString(),
              new Boolean(method.get("deprecated").asString().getString()),
              paramList));
    }
  }

  private String generateComponentsJSON(){
    StringBuilder sb = new StringBuilder();
    sb.append("[");
    String separator = "";
    for(Map.Entry<String, Component> comp : components.entrySet()){
      sb.append(separator).append(comp.getValue().typeDescription);
      separator=",";
    }
    sb.append("]");
    return sb.toString();
  }
  
  @Override
  public boolean isComponent(String componentTypeName) {
    Component component = components.get(componentTypeName);
    if (component == null) {
      return false;
    }

    return true;
  }


}
