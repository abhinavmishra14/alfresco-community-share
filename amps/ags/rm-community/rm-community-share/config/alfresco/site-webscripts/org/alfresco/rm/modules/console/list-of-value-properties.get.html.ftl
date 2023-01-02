<#--
 #%L
 Alfresco Records Management Module
 %%
 Copyright (C) 2005 - 2023 Alfresco Software Limited
 %%
 This file is part of the Alfresco software.
 -
 If the software was purchased under a paid Alfresco license, the terms of
 the paid license agreement will prevail.  Otherwise, the software is
 provided under the following open source license terms:
 -
 Alfresco is free software: you can redistribute it and/or modify
 it under the terms of the GNU Lesser General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.
 -
 Alfresco is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU Lesser General Public License for more details.
 -
 You should have received a copy of the GNU Lesser General Public License
 along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 #L%
-->
<div id="${args.htmlid}-dialog" class="rm-list-of-value-properties">
   <div id="${args.htmlid}-dialogTitle" class="hd">&nbsp;</div>
   <div class="bd">
      <form id="${args.htmlid}-form" action="" method="">
         <div class="yui-gd">
            <div class="yui-u first"><label for="${args.htmlid}-constraintTitle">${msg("label.name")}:</label></div>
            <div class="yui-u"><input id="${args.htmlid}-constraintTitle" type="text" name="constraintTitle" tabindex="0" />&nbsp;*</div>
         </div>
         <div class="bdft">
            <input type="button" id="${args.htmlid}-ok" value="${msg("button.ok")}" tabindex="0" />
            <input type="button" id="${args.htmlid}-cancel" value="${msg("button.cancel")}" tabindex="0" />
         </div>
      </form>
   </div>
</div>
