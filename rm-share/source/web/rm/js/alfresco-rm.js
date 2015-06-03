/**
 * Alfresco RM top-level namespaces.
 */
Alfresco.rm = Alfresco.rm || {};
Alfresco.rm.component = Alfresco.rm.component || {};
Alfresco.rm.module = Alfresco.rm.module || {};
Alfresco.rm.template = Alfresco.rm.component.template || {};
Alfresco.rm.doclib = Alfresco.rm.component.doclib || {};

/**
 * Gets the value for the specified parameter from the URL
 *
 * @method getParamValueFromUrl
 */
Alfresco.rm.getParamValueFromUrl = function(param)
{
   var token,
      result = null,
      hash = window.location.hash,
      params = hash.replace('#', '').split("&");
   for (var i = 0; i < params.length; i++)
   {
      token = params[i].split("=");
      if (token[0] === param)
      {
         result = token[1];
         break;
      }
   }
   return result;
};

/**
 * Header check box click handler for a YUI data table.
 *
 * This functions expects that the check box column
 * is the first column in the table and the table
 * does not allow column dragging.
 *
 * If the header check box is ticked/unticked all check
 * boxes in the same column will be ticked/unticked.
 *
 * @method dataTableHeaderCheckboxClick
 */
Alfresco.rm.dataTableHeaderCheckboxClick = function(oArgs)
{
   var key = this.getColumnSet().headers[0][0];
   if (this.getColumn(oArgs.target).key == key)
   {
      var rs = this.getRecordSet(),
         checked = YAHOO.util.Event.getTarget(oArgs.event).checked;
      for (var i = 0; i < rs.getLength(); i++)
      {
         if (checked !== undefined)
         {
            rs.getRecord(i).setData(key, checked);
            this.getRow(i).cells[0].children[0].firstChild.checked = checked;
         }
      }
      YAHOO.Bubbling.fire("dataTableHeaderCheckboxChange",
      {
         headerCheckBoxChecked: checked
      });
   }
};

/**
 * Cell check box click handler for a YUI data table.
 *
 * This functions expects that the check box column
 * is the first column in the table and the table
 * does not allow column dragging.
 *
 * If a check box in the column is ticked/unticked
 * it will be check if all other check boxes have the
 * same state. If they are all ticked/unticked the
 * header checkbox will also be ticked/unticked.
 *
 * @method dataTableCheckboxClick
 */
Alfresco.rm.dataTableCheckboxClick = function(oArgs)
{
   var key = this.getColumnSet().headers[0][0],
      target = oArgs.target,
      column = this.getColumn(target);
   if (column.key == key)
   {
      var checked = target.checked,
         headerChecked = true,
         atLeastOneChecked = false,
         rs = this.getRecordSet();
      this.getRecord(target).setData(key, checked);
      for (var i = 0; i < rs.getLength(); i++)
      {
         var checkedData = rs.getRecord(i).getData(key);
         if (headerChecked && !checkedData)
         {
            headerChecked = false;
         }
         if (!atLeastOneChecked && checkedData)
         {
            atLeastOneChecked = true;
         }
         if (!headerChecked && atLeastOneChecked)
         {
            break;
         }
      }
      column.getThLinerEl().children[0].firstChild.checked = headerChecked;

      YAHOO.Bubbling.fire("dataTableCheckboxChange",
      {
         checkBoxChecked: checked,
         headerCheckBoxChecked: headerChecked,
         atLeastOneChecked: atLeastOneChecked
      });
   }
};

/**
 * Gets the nodeRefs of selected items in a data table.
 * This helper method is specific for a table with
 * check box column, which has the key 'check'.
 *
 * @method dataTableSelectedItems
 */
Alfresco.rm.dataTableSelectedItems = function(dataTable)
{
   var records = dataTable.getRecordSet().getRecords();
      selectedHolds = [];
   for (var i = 0; i < records.length; i++)
   {
      var record = records[i];
      if (record.getData('check'))
      {
         selectedHolds.push(record.getData('nodeRef'));
      }
   }
   return selectedHolds;
};

/**
 *  Are we currently in an RM Site?
 *
 *  Returns false if:
 *     - the current page is outside of a site context, OR
 *     - the current page is in a site that isn't an RM site.
 */
Alfresco.rm.isRMSite = function()
{
   // FIXME: This needs replacing with something less fragile, see RM-2275
   return (Alfresco.constants.SITE === "rm");
};

/**
 * Checks if the content is classified or not. A content which is
 * classified as "Unclassified" will be treated as not classified.
 *
 * @method isClassified
 */
Alfresco.rm.isClassified = function(jsNode)
{
   var classifiedProp = "clf:currentClassification";
   return jsNode.hasProperty(classifiedProp) && jsNode.properties[classifiedProp].id !== "Unclassified";
};

/**
 * Adds the classified banner if the content has been classified
 *
 * @method classifiedRenderer
 */
Alfresco.rm.classifiedBanner = function(cell, record, msg)
{
   var jsNode = record.getData().jsNode,
   isClassified = Alfresco.rm.isClassified(jsNode);

   if (isClassified)
   {
      cell.innerHTML = '<div class="info-banner">' + msg("banner.classification.info") + ": " + jsNode.properties["clf:currentClassification"].label + '</div>' + cell.innerHTML;
   }
};