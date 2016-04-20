package org.alfresco.web.config.forms;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.extensions.config.ConfigElement;
import org.springframework.extensions.config.ConfigException;
import org.springframework.extensions.config.element.ConfigElementAdapter;

/**
 * Custom config element that represents 'forms' values for the client.
 * 
 * @author Neil McErlean.
 */
public class FormsConfigElement extends ConfigElementAdapter
{
	private static final long serialVersionUID = -7196705568492121773L;

    public static final String FORMS_ID = "forms";
    
    private FormConfigElement defaultFormElement;
    private Map<String, FormConfigElement> formElementsById =
    	    new LinkedHashMap<String, FormConfigElement>();
    private DefaultControlsConfigElement defaultControlsElement;
    private ConstraintHandlersConfigElement constraintHandlersElement;
    private DependenciesConfigElement dependenciesConfigElement;
    
    public FormsConfigElement()
    {
        super(FORMS_ID);
    }

    public FormsConfigElement(String name)
    {
        super(name);
    }

    /**
     * @see ConfigElement#getChildren()
     */
    @Override
    public List<ConfigElement> getChildren()
    {
        throw new ConfigException(
                "Reading the forms config via the generic interfaces is not supported");
    }

    /**
     * This method returns the default form from within a &lt;forms&gt; tag. The default
     * form has no id specified.
     * 
     * @return the default FormConfigElement instance.
     */
    public FormConfigElement getDefaultForm()
    {
    	return this.defaultFormElement;
    }

    /**
     * This method returns the form having the specified id string from within a
     * &lt;forms&gt; tag.
     * 
     * @return the FormConfigElement instance having the correct id, if one exists,
     * else null.
     */
    public FormConfigElement getForm(String id)
    {
    	return this.formElementsById.get(id);
    }

    void setDefaultForm(FormConfigElement formCE)
    {
    	this.defaultFormElement = formCE;
    }
    
    void addFormById(FormConfigElement formCE, String formId)
    {
    	this.formElementsById.put(formId, formCE);
    }

    public DefaultControlsConfigElement getDefaultControls()
    {
        return this.defaultControlsElement;
    }

    void setDefaultControls(DefaultControlsConfigElement defltCtrlsCE)
    {
        this.defaultControlsElement = defltCtrlsCE;
    }
        
    public ConstraintHandlersConfigElement getConstraintHandlers()
    {
        return this.constraintHandlersElement;
    }

    void setConstraintHandlers(ConstraintHandlersConfigElement constraintHandlersCE)
    {
        this.constraintHandlersElement = constraintHandlersCE;
    }
        
    public DependenciesConfigElement getDependencies()
    {
        return this.dependenciesConfigElement;
    }

    void setDependencies(DependenciesConfigElement dependenciesCS)
    {
        this.dependenciesConfigElement = dependenciesCS;
    }
        
    /**
     * @see ConfigElement#combine(org.springframework.extensions.config.ConfigElement)
     */
    @Override
    public ConfigElement combine(ConfigElement otherConfigElement)
    {
        FormsConfigElement otherFormsElem = (FormsConfigElement)otherConfigElement;

        FormsConfigElement result = new FormsConfigElement();

        ConfigElement combinedDefaultForm;
        if (this.getDefaultForm() == null)
        {
            combinedDefaultForm = otherFormsElem.getDefaultForm();
        }
        else
        {
            combinedDefaultForm = this.defaultFormElement.combine(otherFormsElem.getDefaultForm());
        }
        
        result.setDefaultForm((FormConfigElement)combinedDefaultForm);
        
        for (String thisFormId : this.formElementsById.keySet())
        {
        	if (otherFormsElem.formElementsById.containsKey(thisFormId))
        	{
        		FormConfigElement otherFormCE = otherFormsElem.getForm(thisFormId);
        		FormConfigElement combinedElement = (FormConfigElement)formElementsById.get(thisFormId).combine(otherFormCE);
        		result.addFormById(combinedElement, thisFormId);
        	}
        	else
        	{
        		result.addFormById(this.formElementsById.get(thisFormId), thisFormId);
        	}
        }
        for (String otherFormId : otherFormsElem.formElementsById.keySet())
        {
        	if (this.formElementsById.containsKey(otherFormId))
        	{
        		// Ignore it. The combination was handled in the previous loop.
        	}
        	else
        	{
        		result.addFormById(otherFormsElem.formElementsById.get(otherFormId), otherFormId);
        	}
        }
        
        // Combine default-controls
        ConfigElement combinedDefaultControls = this.defaultControlsElement == null ?
                otherFormsElem.getDefaultControls()
                : this.defaultControlsElement.combine(otherFormsElem.getDefaultControls());
        result.setDefaultControls((DefaultControlsConfigElement)combinedDefaultControls);

        // Combine constraint-handlers
        ConfigElement combinedConstraintHandlers = this.constraintHandlersElement == null ?
                otherFormsElem.getConstraintHandlers()
                : this.constraintHandlersElement.combine(otherFormsElem.getConstraintHandlers());
        result.setConstraintHandlers((ConstraintHandlersConfigElement)combinedConstraintHandlers);
        
        // Combine dependencies
        ConfigElement combinedDependencies = this.dependenciesConfigElement == null ?
                otherFormsElem.getDependencies()
                : this.dependenciesConfigElement.combine(otherFormsElem.getDependencies());
        result.setDependencies((DependenciesConfigElement)combinedDependencies);
        
        return result;
    }
}