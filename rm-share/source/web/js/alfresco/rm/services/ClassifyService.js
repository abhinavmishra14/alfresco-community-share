/**
 * Copyright (C) 2005-2015 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * This Aikau Service uses the classify REST APIs
 *
 * @module rm/services/ClassifyService
 * @extends module:alfresco/core/Core
 * @extends module:alfresco/service/crudService
 * @mixes module:alfresco/core/CoreXhr
 * @author David Webster
 * @author Tuna Aksoy
 * @since 2.4.a
 *
 * @event RM_CLASSIFY_REASONS_GET
 */

define(["dojo/_base/declare",
      "alfresco/core/Core",
      "alfresco/core/CoreXhr",
      "alfresco/services/CrudService",
      "service/constants/Default",
      "alfresco/core/NodeUtils",
      "dijit/registry",
      "dojo/dom-style",
      "dojo/_base/lang"],
   function (declare, AlfCore, AlfXhr, CrudService, AlfConstants, NodeUtils, registry, domStyle, lang) {

      return declare([AlfCore, AlfXhr, CrudService], {

         /**
          * Scope the message keys used in this service
          *
          * @instance
          * @type String
          * @default "org.alfresco.rm.ClassifyService"
          */
         i18nScope: "org.alfresco.rm.ClassifyService",

         /**
          * An array of the i18n files to use with this service.
          *
          * @instance
          * @type {object[]}
          * @default [{i18nFile: "./i18n/ClassifyService.properties"}]
          */
         i18nRequirements: [{i18nFile: "./i18n/ClassifyService.properties"}],

         /**
          * An array of the CSS files to use with this service.
          *
          * @instance
          * @type {object[]}
          * @default [{cssFile:"./css/ClassifyService.css"}]
          */
         cssRequirements: [{cssFile:"./css/ClassifyService.css"}],

         /**
          * URL used to get classification reasons.
          *
          * @instance
          * @type {string}
          * @default
          */
         reasonsAPIGet: "api/classification/reasons",

         /**
          * URL used to get classification levels.
          *
          * @instance
          * @type {string}
          * @default
          */
         levelsAPIGet: "api/classification/levels",

         /**
          * URL used to get exemption categories.
          *
          * @instance
          * @type {string}
          * @default
          */
         exemptionsAPIGet: "/api/classification/exemptioncategories",

         /**
          * URL used to classify/edit content. Parse through lang.mixin for token substitution.
          *
          * @instance
          * @type {string}
          * @default
          */
         classifyAPICreateUpdate: "api/node/{nodeRefUrl}/classify",

         /**
          * @instance
          * @param {array} args Constructor arguments
          *
          * @listens RM_CLASSIFY_REASONS_GET
          * @listens RM_CLASSIFY_CONTENT
          * @listens RM_EDIT_CLASSIFIED_CONTENT
          * @listens RM_CLASSIFY
          * @listens RM_EDIT_CLASSIFIED
          * @listens ALF_CLASSIFY_VALIDATE_CLASSIFY_BY
          * @listens ALF_CLASSIFY_VALIDATE_DOWNGRADE_INSTRUCTIONS
          * @listens LEVEL_CHANGE_EDIT_valueChangeOf_LEVELS
          */
         constructor: function rm_services_classifyService__constructor(args) {
            this.alfSubscribe("RM_CLASSIFY_REASONS_GET", lang.hitch(this, this.onGetReasons));
            this.alfSubscribe("RM_CLASSIFY_EXEMPTIONS_GET", lang.hitch(this, this.onGetExemptions));
            this.alfSubscribe("RM_CLASSIFY_CONTENT", lang.hitch(this, this.onClassifyContent));
            this.alfSubscribe("RM_EDIT_CLASSIFIED_CONTENT", lang.hitch(this, this.onEditClassifiedContent));
            this.alfSubscribe("RM_CLASSIFY", lang.hitch(this, this.onCreate));
            this.alfSubscribe("RM_EDIT_CLASSIFIED", lang.hitch(this, this.onUpdate));
            this.alfSubscribe("ALF_CLASSIFY_VALIDATE_CLASSIFY_BY", lang.hitch(this, this.onValidateClassifiedBy));
            this.alfSubscribe("ALF_CLASSIFY_VALIDATE_DOWNGRADE_INSTRUCTIONS", lang.hitch(this, this.onValidateDowngradeInstructions));
            this.alfSubscribe("LEVEL_CHANGE_EDIT_valueChangeOf_LEVELS", lang.hitch(this, this.onLevelChange));
         },

         /**
          * Displays a notification information when classification level is changed.
          * And makes the reclassify by and reclassification reason fields mandatory.
          * It also populates the reclassify by field with current user name and
          * clears the reclassification reason field.
          *
          * @param payload
          */
         onLevelChange: function rm_services_classifyService__onLevelChange(payload) {
            var levels = registry.byId("LEVELS_EDIT"),
               notificationInfo = registry.byId("NOTIFICATION_INFO_EDIT"),
               reclassifyBy = registry.byId("RECLASSIFY_BY_EDIT"),
               reclassifyReason = registry.byId("RECLASSIFY_REASON_EDIT"),
               notificationInfoDomNode = notificationInfo.domNode;

            if (payload.oldValue === "" || levels.value === payload.value)
            {
               domStyle.set(notificationInfoDomNode, "display", "none");

               reclassifyBy.alfDisabled(true);
               reclassifyBy.alfRequired(false);

               reclassifyReason.alfDisabled(true);
               reclassifyReason.alfRequired(false);
            }
            else
            {
               var options = levels.options;

               if (options !== null)
               {
                  var length = options.length,
                     action;

                  if (payload.value === options[length - 1].value)
                  {
                     action = this.message("label.edit.classified.content.declassify");
                  }
                  else
                  {
                     var originalIndex;

                     for (var i = 0; i < options.length; i++)
                     {
                        if (payload.value === options[i].value)
                        {
                           originalIndex = i;
                           break;
                        }
                     }

                     var newIndex;

                     for (var j = 0; j < options.length; j++)
                     {
                        if (levels.value === options[j].value)
                        {
                           newIndex = j;
                           break;
                        }
                     }

                     if (originalIndex > newIndex)
                     {
                        action = this.message("label.edit.classified.content.downgrade");
                     }
                     else
                     {
                        action = this.message("label.edit.classified.content.upgrade");
                     }
                  }

                  notificationInfoDomNode.innerHTML = this.message("label.edit.classified.content.reclassificationInformation", {
                     0: action
                  });

                  domStyle.set(notificationInfoDomNode, "display", "");

                  reclassifyBy.setValue(Alfresco.constants.USER_FULLNAME);
                  reclassifyBy.alfRequired(true);
                  reclassifyBy.alfDisabled(false);

                  reclassifyReason.setValue(null);
                  reclassifyReason.alfRequired(true);
                  reclassifyReason.alfDisabled(false);
                  reclassifyReason.startValidation();
               }
            }
         },

         /**
          * Get all the classification reasons for the given node.
          *
          * @param payload
          */
         onGetReasons: function rm_services_classifyService__onGetReasons(payload) {
            if (payload && payload.alfResponseTopic) {
               var url = AlfConstants.PROXY_URI + this.reasonsAPIGet;
               this.serviceXhr({
                  url: url,
                  method: "GET",
                  alfTopic: payload.alfResponseTopic
               });
            }
            else
            {
               this.alfLog("error", "A request to get the classification reasons but the 'responseTopic' attributes was not provided in the payload", payload);
            }
         },

         /**
          * Get all the declassification exemptions
          */
         onGetExemptions: function rm_services_classifyService__onGetExemptions(payload) {
            if (payload && payload.alfResponseTopic)
            {
               var url = AlfConstants.PROXY_URI + this.exemptionsAPIGet;
               this.serviceXhr({
                  url: url,
                  method: "GET",
                  alfTopic: payload.alfResponseTopic
               });
            }
            else
            {
               this.alfLog("error", "A request to get the declassification exemptions but the 'responseTopic' attributes was not provided in the payload", payload);
            }
         },

         /**
          * Helper method for creating publication events for classify/edit content dialogs
          *
          * @param configObject
          * @param payload
          *
          * @fires ALF_CREATE_FORM_DIALOG_REQUEST
          * @fires RM_CLASSIFY_REASONS_GET
          * @fires RM_CLASSIFY_EXEMPTIONS_GET
          * @fires ALF_GET_FORM_CONTROL_OPTIONS
          */
         _publishClassificationFormDialogRequest: function rm_services_classifyService___publishClassificationFormDialogRequest(configObject, payload) {
            var dialogTitle = (Alfresco.rm.isRMSite(payload.item.location.site)) ? configObject.dialogTitleRm : configObject.dialogTitleCollab;

            this.alfPublish("ALF_CREATE_FORM_DIALOG_REQUEST", {
               dialogId: configObject.dialogId,
               dialogTitle: this.message(dialogTitle),
               dialogConfirmationButtonTitle: this.message(configObject.dialogConfirmationButtonTitle),
               dialogConfirmationButtonId: configObject.dialogConfirmationButtonId,
               dialogCancellationButtonTitle: this.message("label.button.cancel"),
               dialogCancellationButtonId: "CANCEL",
               formSubmissionTopic: configObject.formSubmissionTopic,
               formSubmissionPayloadMixin: {
                  nodeRef: payload.item.nodeRef
               },
               widgets: [
                  {
                     id: "LEVELS" + configObject.notificationAction,
                     name: "alfresco/forms/controls/Select",
                     config: {
                        fieldId: "LEVELS",
                        label: this.message("label.classify.levels"),
                        name: "classificationLevelId",
                        requirementConfig: {
                           initialValue: true
                        },
                        pubSubScope: "LEVEL_CHANGE" + configObject.notificationAction,
                        value: configObject.levelsValue,
                        optionsConfig: {
                           publishTopic: "ALF_GET_FORM_CONTROL_OPTIONS",
                           publishPayload: {
                              url: AlfConstants.PROXY_URI + this.levelsAPIGet,
                              itemsAttribute: "data.items",
                              labelAttribute: "displayLabel",
                              valueAttribute: "id"
                           }
                        }
                     }
                  },{
                     id: "CLASSIFIED_BY",
                     name: "alfresco/forms/controls/TextBox",
                     config: {
                        label: this.message("label.classify.by"),
                        name: "classifiedBy",
                        value: configObject.classifiedByValue,
                        requirementConfig: {
                           initialValue: true
                        },
                        validationConfig: [
                           {
                              validation: "validationTopic",
                              validationTopic: "ALF_CLASSIFY_VALIDATE_CLASSIFY_BY",
                              errorMessage: this.message("label.classify.classifiedBy.validation.error")
                           }
                        ]
                     }
                  },{
                     id: "RECLASSIFY_BY" + configObject.notificationAction,
                     name: "alfresco/forms/controls/TextBox",
                     config: {
                        label: this.message("label.classify.reclassifyBy"),
                        name: "reclassifyBy",
                        _disabled: true,
                        postWhenHiddenOrDisabled: false,
                        visibilityConfig: {
                           initialValue: configObject.visibilityReclassification
                        }
                     }
                  },{
                     id: "RECLASSIFY_REASON" + configObject.notificationAction,
                     name: "alfresco/forms/controls/TextArea",
                     config: {
                        label: this.message("label.classify.reclassifyReason"),
                        name: "reclassifyReason",
                        _disabled: true,
                        postWhenHiddenOrDisabled: false,
                        visibilityConfig: {
                           initialValue: configObject.visibilityReclassification
                        }
                     }
                  },{
                     id: "LAST_RECLASSIFY_BY" + configObject.notificationAction,
                     name: "alfresco/forms/controls/TextBox",
                     config: {
                        label: this.message("label.classify.lastReclassifyBy"),
                        name: "lastReclassifyBy",
                        value: configObject.lastReclassifyBy,
                        visibilityConfig: {
                           initialValue: configObject.visibilityLastReclassification
                        },
                        inlineHelp: this.message("label.classify.lastReclassifyBy.help")
                     }
                  },{
                     id: "LAST_RECLASSIFY_REASON" + configObject.notificationAction,
                     name: "alfresco/forms/controls/TextArea",
                     config: {
                        label: this.message("label.classify.lastReclassifyReason"),
                        name: "lastReclassifyReason",
                        value: configObject.lastReclassifyReason,
                        visibilityConfig: {
                           initialValue: configObject.visibilityLastReclassification
                        }
                     }
                  },{
                     id: "AGENCY",
                     name: "alfresco/forms/controls/TextBox",
                     config: {
                        label: this.message("label.classify.agency"),
                        name: "classificationAgency",
                        value: configObject.agencyValue,
                        inlineHelp: this.message("label.classify.agency.help")
                     }
                  },{
                     id: "REASONS",
                     name: "alfresco/forms/controls/MultiSelectInput",
                     config: {
                        label: this.message("label.classify.reasons"),
                        name: "classificationReasons",
                        width: "362px",
                        requirementConfig: {
                           initialValue: true
                        },
                        value: configObject.reasonsValue,
                        optionsConfig: {
                           queryAttribute: "fullReason",
                           valueAttribute: "id",
                           labelAttribute: "fullReason",
                           labelFormat: {
                              choice: "{value}"
                           },
                           publishTopic: "RM_CLASSIFY_REASONS_GET",
                           publishPayload: {
                              alfResponseTopic: this.generateUuid(),
                              resultsProperty: "response.data.items"
                           },
                           searchStartsWith: false
                        }
                     }
                  },{
                     id: "TAB_CONTAINER",
                     name: "alfresco/forms/TabbedControls",
                     config: {
                        widgets: [{
                           id: "DOWNGRADE_SCHEDULE",
                           title: this.message("label.classify.downgradeSchedule"),
                           name: "alfresco/forms/ControlColumn",
                           config: {
                              widgets: [{
                                 id: "DOWNGRADE_DATE",
                                 name: "alfresco/forms/controls/DateTextBox",
                                 config: {
                                    fieldId: "DOWNGRADE_SCHEDULE_FIELD",
                                    label: this.message("label.classify.downgradeDate"),
                                    name: "downgradeDate",
                                    value: configObject.downgradeDate,
                                    noPostWhenValueIs: [null]
                                 }
                              },{
                                 id: "DOWNGRADE_EVENT",
                                 name: "alfresco/forms/controls/TextBox",
                                 config: {
                                    fieldId: "DOWNGRADE_EVENT_FIELD",
                                    label: this.message("label.classify.downgradeEvent"),
                                    name: "downgradeEvent",
                                    value: configObject.downgradeEvent,
                                    inlineHelp: this.message("label.classify.downgradeEvent.help")
                                 }
                              },{
                                 id: "DOWNGRADE_INSTRUCTIONS",
                                 name: "alfresco/forms/controls/TextArea",
                                 config: {
                                    label: this.message("label.classify.downgradeInstructions"),
                                    name: "downgradeInstructions",
                                    value: configObject.downgradeInstructions,
                                    requirementConfig: {
                                       initialValue: false,
                                       rulesMethod: "ANY",
                                       rules: [{
                                          targetId: "DOWNGRADE_SCHEDULE_FIELD",
                                          is: ["^\\s+$"]
                                       },{
                                          targetId: "DOWNGRADE_EVENT_FIELD",
                                          is: ["^\\s+$"]
                                       }]
                                    },
                                    ruleValueComparator: function(currentValue, targetValue)
                                    {
                                       return currentValue && !currentValue.toString().match(targetValue);
                                    },
                                    validationConfig: [{
                                       validation: "validationTopic",
                                       validationTopic: "ALF_CLASSIFY_VALIDATE_DOWNGRADE_INSTRUCTIONS"
                                    }]
                                 }
                              }]
                           }
                        },{
                           id: "DECLASSIFICATION_SCHEDULE",
                           title: this.message("label.classify.declassificationSchedule"),
                           name: "alfresco/forms/ControlColumn",
                           config: {
                              widgets: [{
                                 id: "DECLASSIFICATION_DATE",
                                 name: "alfresco/forms/controls/DateTextBox",
                                 config: {
                                    label: this.message("label.classify.declassificationDate"),
                                    name: "declassificationDate",
                                    value: configObject.declassificationDate,
                                    noPostWhenValueIs: [null]
                                 }
                              },{
                                 id: "DECLASSIFICATION_EVENT",
                                 name: "alfresco/forms/controls/TextBox",
                                 config: {
                                    label: this.message("label.classify.declassificationEvent"),
                                    name: "declassificationEvent",
                                    value: configObject.declassificationEvent,
                                    inlineHelp: this.message("label.classify.declassificationEvent.help")
                                 }
                              },{
                                 id: "EXEMPTIONS",
                                 name: "alfresco/forms/controls/MultiSelectInput",
                                 config: {
                                    label: this.message("label.classify.declassificationExemptions"),
                                    name: "declassificationExemptions",
                                    width: "362px",
                                    value: configObject.declassificationExemptions,
                                    optionsConfig: {
                                       queryAttribute: "fullCategory",
                                       valueAttribute: "id",
                                       labelAttribute: "fullCategory",
                                       labelFormat: {
                                          choice: "{value}"
                                       },
                                       publishTopic: "RM_CLASSIFY_EXEMPTIONS_GET",
                                       publishPayload: {
                                          alfResponseTopic: this.generateUuid(),
                                          resultsProperty: "response.data.items"
                                       },
                                       searchStartsWith: false
                                    }
                                 }
                              }]
                           }
                        }]
                     }
                  },{
                     id: "NOTIFICATION_INFO" + configObject.notificationAction,
                     name: "alfresco/renderers/Banner",
                     config: {
                        bannerMessage: configObject.visibilityReclassification ? "label.edit.classified.content.reclassificationInformation" : null,
                        additionalCssClasses: "reclassification-information"
                     }
                  }
               ]
            }, true);
         },

         /**
          * Triggered by the classify document and classify record actions. Sets up data for dialog
          *
          * @instance
          * @param payload
          *
          * @fires RM_CLASSIFY
          */
         onClassifyContent: function rm_services_classifyService__onClassifyContent(payload) {
            var configObject = {};
            configObject.dialogTitleRm = "label.classify.dialog.title.rm";
            configObject.dialogTitleCollab = "label.classify.dialog.title";
            configObject.dialogId = "CLASSIFY_CONTENT_DIALOG";
            configObject.dialogConfirmationButtonTitle = "label.button.create";
            configObject.dialogConfirmationButtonId = "OK";
            configObject.formSubmissionTopic = "RM_CLASSIFY";
            configObject.levelsValue = "";
            configObject.classifiedByValue = Alfresco.constants.USER_FULLNAME;
            configObject.visibilityReclassification = false;
            configObject.visibilityLastReclassification = false;
            configObject.notificationAction = "";

            this._publishClassificationFormDialogRequest(configObject, payload);
         },

         /**
          * Triggered by the edit classified file/record actions. Sets up data for dialog
          *
          * @instance
          * @param payload
          *
          * @fires RM_EDIT_CLASSIFIED
          */
         onEditClassifiedContent: function rm_services_classifyService__onEditClassifiedContent(payload) {
            var configObject = {},
               properties = payload.item.node.properties;

            configObject.dialogTitleRm = "label.edit.classification.dialog.title.rm";
            configObject.dialogTitleCollab = "label.edit.classification.dialog.title";
            configObject.dialogId = "EDIT_CLASSIFIED_CONTENT_DIALOG";
            configObject.dialogConfirmationButtonTitle = "label.button.save";
            configObject.dialogConfirmationButtonId = "Edit";
            configObject.formSubmissionTopic = "RM_EDIT_CLASSIFIED";
            configObject.levelsValue = properties["clf_currentClassification"].id;
            configObject.classifiedByValue = properties["clf_classifiedBy"];
            configObject.agencyValue = properties["clf_classificationAgency"];
            configObject.reasonsValue = properties["clf_classificationReasons"];
            configObject.downgradeDate = properties["clf_downgradeDate"] && properties["clf_downgradeDate"].iso8601;
            configObject.downgradeEvent = properties["clf_downgradeEvent"];
            configObject.downgradeInstructions = properties["clf_downgradeInstructions"];
            configObject.declassificationDate = properties["clf_declassificationDate"] && properties["clf_declassificationDate"].iso8601;
            configObject.declassificationEvent = properties["clf_declassificationEvent"];
            configObject.declassificationExemptions = properties["clf_declassificationExemptions"];
            configObject.lastReclassifyBy = properties["clf_lastReclassifyBy"];
            configObject.lastReclassifyReason = properties["clf_lastReclassifyReason"];
            configObject.visibilityReclassification = true;
            configObject.visibilityLastReclassification = (configObject.lastReclassifyBy || configObject.lastReclassifyReason) ? true : false;
            configObject.notificationAction = "_EDIT";

            this._publishClassificationFormDialogRequest(configObject, payload);
         },

         /**
          * Helper method for the classify/edit content actions.
          *
          * @param payload
          * @param successMessage
          * @param failureMessage
          */
         _onClassifyAction: function rm_services_classifyService___onClassifyAction(payload, successMessage, failureMessage) {
            if (!payload.nodeRef)
            {
               this.alfLog("error", "nodeRef required");
            }

            // Update the payload
            payload.nodeRefUrl = NodeUtils.processNodeRef(payload.nodeRef).uri;
            payload = lang.mixin(payload, {
               url: lang.replace(this.classifyAPICreateUpdate, payload),
               successMessage: this.message(successMessage),
               failureMessage: this.message(failureMessage)
            });
         },

         /**
          * Classifies the given content.
          *
          * @param payload
          */
         onCreate: function rm_services_classifyService__onCreate(payload) {
            this._onClassifyAction(payload, "label.classify.content.success", "label.classify.content.failure");

            this.inherited(arguments);
         },

         /**
          * Edits the classified content
          *
          * @param payload
          */
         onUpdate: function rm_services_classifyService__onUpdate(payload) {
            this._onClassifyAction(payload, "label.edit.classified.content.success", "label.edit.classified.content.failure");

            this.inherited(arguments);
         },

         /**
          * Used to validate the classified by field.
          *
          * @param payload
          */
         onValidateClassifiedBy: function rm_services_classifyService__onValidateClassifiedBy(payload) {
            // Classified By field MUST NOT start with a whitespace nor can it consist of only whitespaces. RM-2373
            var isValid = payload.value.length === lang.trim(payload.value).length && lang.trim(payload.value).length !== 0;

            this.alfPublish(payload.alfResponseTopic, {isValid: isValid});
         },

         /**
          * Used to validate the downgrade instructions field.
          *
          * @param payload
          */
         onValidateDowngradeInstructions: function rm_services_classifyService__onValidateDowngradeInstructions(payload) {
            var isValid = true,
               downgradeEvent = registry.byId("DOWNGRADE_EVENT"),
               downgradeDate = registry.byId("DOWNGRADE_DATE");

            if (downgradeEvent && downgradeDate)
            {
               var downgradeEventValue = registry.byId("DOWNGRADE_EVENT").getValue(),
                  downgradeDateValue = registry.byId("DOWNGRADE_DATE").getValue();

               if (((downgradeEventValue && lang.trim(downgradeEventValue).length > 0) || (downgradeDateValue && lang.trim(downgradeDateValue).length > 0)) &&
                     !(payload && lang.trim(payload.value).length > 0))
               {
                  isValid = false;
               }
            }

            this.alfPublish(payload.alfResponseTopic, {isValid: isValid});
         }
      });
   });
