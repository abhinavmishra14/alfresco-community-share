/*
 * #%L
 * Alfresco Records Management Module
 * %%
 * Copyright (C) 2005 - 2021 Alfresco Software Limited
 * %%
 * This file is part of the Alfresco software.
 * -
 * If the software was purchased under a paid Alfresco license, the terms of
 * the paid license agreement will prevail.  Otherwise, the software is
 * provided under the following open source license terms:
 * -
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * -
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * -
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

package org.alfresco.module.org_alfresco_module_rm_share.evaluator;

import org.alfresco.error.AlfrescoRuntimeException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * Check for the validity of Records Management indicators
 *
 * @author: mikeh
 */
public class IndicatorEvaluator extends BaseRMEvaluator
{
    private String indicator;

    private boolean expected = true;

    /**
     * Sets the name of the indicator to check for
     *
     * @param indicator The name of the indicator to check for
     */
    public void setIndicator(String indicator)
    {
        this.indicator = indicator;
    }

    public void setExpected(boolean expected)
    {
        this.expected = expected;
    }

    @Override
    public boolean evaluate(JSONObject jsonObject)
    {
        if (indicator == null)
        {
            return false;
        }

        boolean result = false;

        try
        {
            JSONArray indicators = getRMIndicators(jsonObject);
            if (indicators != null && indicators.contains(indicator))
            {
                result = true;
            }
        }
        catch (Exception err)
        {
            throw new AlfrescoRuntimeException("Exception whilst running UI evaluator: " + err);
        }

        return (result == expected);
    }
}