/*
 * SonarQube
 * Copyright (C) 2009-2017 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonarqube.ws.client.components;

import java.util.List;
import javax.annotation.Generated;

/**
 * Returns a component (file, directory, project, view?) and its ancestors. The ancestors are ordered from the parent to the root project. The 'componentId' or 'component' parameter must be provided.<br>Requires the following permission: 'Browse' on the project of the specified component.
 *
 * This is part of the internal API.
 * This is a POST request.
 * @see <a href="https://next.sonarqube.com/sonarqube/web_api/api/components/show">Further information about this action online (including a response example)</a>
 * @since 5.4
 */
@Generated("https://github.com/SonarSource/sonar-ws-generator")
public class ShowRequest {

  private String branch;
  private String component;
  private String componentId;

  /**
   * Branch key
   *
   * This is part of the internal API.
   * Example value: "feature/my_branch"
   */
  public ShowRequest setBranch(String branch) {
    this.branch = branch;
    return this;
  }

  public String getBranch() {
    return branch;
  }

  /**
   * Component key
   *
   * Example value: "my_project"
   */
  public ShowRequest setComponent(String component) {
    this.component = component;
    return this;
  }

  public String getComponent() {
    return component;
  }

  /**
   * Component id
   *
   * Example value: "AU-Tpxb--iU5OvuD2FLy"
   * @deprecated since 6.4
   */
  @Deprecated
  public ShowRequest setComponentId(String componentId) {
    this.componentId = componentId;
    return this;
  }

  public String getComponentId() {
    return componentId;
  }
}
