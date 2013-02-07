/*
 * Copyright (C) 2003-2011 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.wcm.ext.component.activity.listener;

import javax.jcr.Node;
import javax.jcr.Value;

import org.apache.commons.lang.StringUtils;
import org.exoplatform.services.listener.Event;
import org.exoplatform.services.listener.Listener;

/**
 * Created by The eXo Platform SAS Author : eXoPlatform exo@exoplatform.com Mar
 * 15, 2011
 */
public class ContentUpdateActivityListener extends Listener<Node, String> {

  private String[]  editedField        = {"exo:title", "exo:summary", "dc:title", "dc:description", "exo:text"};
  private String[]  bundleMessage      = {"SocialIntegration.messages.editTitle",
                                          "SocialIntegration.messages.editSummary",
                                          "SocialIntegration.messages.editTitle",
                                          "SocialIntegration.messages.editSummary",
                                          "SocialIntegration.messages.editContent"};
  private String[]  bundleMessageEmpty = {"SocialIntegration.messages.emptyTitle",
                                          "SocialIntegration.messages.emptySummary",
                                          "SocialIntegration.messages.emptyTitle",
                                          "SocialIntegration.messages.emptySummary",
                                          "SocialIntegration.messages.emptyContent"};
  private boolean[] needUpdate      = {true, true, true, true, false};
  private int consideredFieldCount = editedField.length;
  /**
   * Instantiates a new post edit content event listener.
   */
  @Override
  public void onEvent(Event<Node, String> event) throws Exception {
    Node currentNode = event.getSource();
    String propertyName = event.getData();
    String newValue;
    try {
      if (!currentNode.hasProperty(propertyName)) return;
      if(currentNode.getProperty(propertyName).getDefinition().isMultiple()){
        StringBuffer sb = new StringBuffer();
        Value[] values = currentNode.getProperty(propertyName).getValues();
        for (int i=0; i<values.length; i++) {
          if (i==0) {
            sb.append(values[i].getString());
          }else {
            sb.append(", ").append(values[i].getString());
          }
        }
        newValue = sb.toString();
      }else {
        newValue= currentNode.getProperty(propertyName).getString();
        if (newValue==null) newValue =""; 
      }
    }catch (Exception e) {
      newValue = "";
    }
    for (int i=0; i< consideredFieldCount; i++) {
      if (propertyName.equals(editedField[i])) {
        if (propertyName.equals("exo:summary")) newValue = Utils.getFirstSummaryLines(newValue);
        if (StringUtils.isEmpty(newValue)) {
          Utils.postActivity(currentNode, bundleMessageEmpty[i], needUpdate[i], true, "");
        } else {
          Utils.postActivity(currentNode, bundleMessage[i], needUpdate[i], true, newValue);
        }
        break;
      }
    }
  }
}
