package org.alfresco.po.share.site.contentrule.createrules.selectors.impl;

import org.alfresco.po.share.site.contentrule.createrules.selectors.AbstractWhenSelector;
import org.openqa.selenium.support.FindBy;

/**
 * User: aliaksei.bul
 * Date: 08.07.13
 * Time: 12:08
 */
@FindBy(tagName="form")
public class WhenSelectorImpl extends AbstractWhenSelector
{
    private enum WhenOptions
    {
        INBOUND(0), UPDATE(1), OUTBOUND(2);

        private final int numberPosition;

        WhenOptions(int numberPosition)
        {
            this.numberPosition = numberPosition;
        }
    }

    public void selectInbound()
    {
        selectWhenOption(WhenOptions.INBOUND.numberPosition);
    }

    public void selectOutbound()
    {
        selectWhenOption(WhenOptions.OUTBOUND.numberPosition);
    }

    public void selectUpdate()
    {
        selectWhenOption(WhenOptions.UPDATE.numberPosition);
    }
}
