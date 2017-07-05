package org.alfresco.po.adminconsole.supporttools;

import org.alfresco.po.adminconsole.AdminConsolePage;
import org.alfresco.po.adminconsole.supporttools.Node.NodeChild;
import org.alfresco.po.adminconsole.supporttools.Node.NodeProperty;
import org.alfresco.utility.exception.TestConfigurationException;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import org.testng.Assert;
import org.testng.asserts.SoftAssert;
import ru.yandex.qatools.htmlelements.element.Select;
import ru.yandex.qatools.htmlelements.element.Table;

import java.util.ArrayList;
import java.util.List;

@PageObject
public class NodeBrowserQueryPage extends AdminConsolePage<NodeBrowserQueryPage> {
    @Override
    protected String relativePathToURL() {
        return "alfresco/s/admin/admin-nodebrowser";
    }

    @Override
    public String getInfoPage() {
        return "";
    }

    @Override
    public String getIntroPage() {
        return "";
    }

    public enum Store {
        archiveSpaceStore("archive://SpacesStore"),
        systemSystem("system://system"),
        userAlfrescoUserStore("user://alfrescoUserStore"),
        workspaceLightWeightVersionStore("workspace://lightWeightVersionStore"),
        workspaceSpaceStore("workspace://SpacesStore"),
        workspaceVersion2Store("workspace://version2Store");
        private String value;

        private Store(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    public enum Actions {
        DELETE("Delete"),
        FORCEDELETE("Force Delete"),
        TAKEOWNERSHIP("Take Ownership"),
        REVERTPERMISSIONS("Revert Permissions");

        private String action;

        private Actions(String action) {
            this.action = action;
        }

        public String getAction() {
            return action;
        }
    }

    public enum Query {
        noderef("noderef"), ftsAlfresco("fts-alfresco");
        private String value;

        private Query(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    @RenderWebElement
    @FindBy(name = "nodebrowser-store")
    Select selectNode;

    @RenderWebElement
    @FindBy(name = "nodebrowser-search")
    Select selectQuery;

    @RenderWebElement
    @FindBy(id = "query")
    WebElement query;

    @RenderWebElement
    @FindBy(css = "input[value='Execute']")
    WebElement execute;

    @FindBy(css = "input[value='Root List']")
    WebElement rootList;

    @FindBy(css = "a[class='action toggler']")
    WebElement searchAdvancedSettings;

    @FindBy(id = "properties-table")
    Table propertiesTable;

    @FindBy(id = "child-table")
    Table childrenTable;

    @FindBy(id="aspects-table")
    Table aspectsTable;

    @FindBy(id="info-table")
    Table nodeInformationTable;

    @FindBy(name ="nodebrowser-query-maxresults")
    WebElement maxResultsFiled;

    @FindBy( name ="nodebrowser-query-skipcount")
    WebElement skipCountField;

    public NodeBrowserQueryPage usingStore(Store store) {
        selectNode.selectByValue(store.getValue());
        return this;
    }

    public NodeBrowserQueryPage execute(String query) {
        this.query.clear();
        this.query.sendKeys(query);
        execute.click();
        return this;
    }

    public NodeBrowserQueryPage usingQuery(Query query) {
        selectQuery.selectByValue(query.getValue());
        return this;
    }

    public boolean isMaxResultsFieldDisplayed()
    {
        return getBrowser().isElementDisplayed(maxResultsFiled);
    }

    public boolean isSkipCountFieldDisplayed()
    {
        return getBrowser().isElementDisplayed(skipCountField);
    }

    public NodeBrowserQueryPage clickSearchAdvancedSettings() {
        getBrowser().waitUntilElementClickable(searchAdvancedSettings).click();
        return this;
    }

    public void setMaxResults(int maxResults ){
        if(!isMaxResultsFieldDisplayed()) {
            searchAdvancedSettings.click();
        }
        getBrowser().waitUntilElementVisible(maxResultsFiled).clear();
        maxResultsFiled.sendKeys(Integer.toString(maxResults));
    }

    public void setSkipCount(int skipCount){
        if(!isSkipCountFieldDisplayed()) {
            searchAdvancedSettings.click();
        }
        getBrowser().waitUntilElementVisible(skipCountField).clear();
        skipCountField.sendKeys(Integer.toString(skipCount));
    }

    public List<NodeProperty> getProperties() {
        ArrayList<NodeProperty> properties = new ArrayList<NodeProperty>();
        List<List<WebElement>> rows = propertiesTable.getRows();
        for (List<WebElement> rowInfo : rows) {
            NodeProperty np = new NodeProperty(rowInfo, getBrowser());
            LOG.info("Property: "+np.toString());
            properties.add(np);
        }

        return properties;
    }

    public List<NodeChild> getChildren() {
        ArrayList<NodeChild> child = new ArrayList<NodeChild>();
        List<List<WebElement>> rows = childrenTable.getRows();
        for (List<WebElement> childRow : rows) {
            NodeChild nc = new NodeChild(childRow, getBrowser());
            LOG.info("Child: " + nc.toString());
            child.add(nc);
        }

        return child;
    }

    public void clickRootList()
    {
        getBrowser().waitUntilElementClickable(rootList).click();
        getBrowser().waitUntilElementIsVisibleWithRetry(By.xpath("//text()[contains(.,'Node Information')]"), 3);
    }

    public List<String> getAspects() {
        ArrayList<String> aspects = new ArrayList<String>();
        List<List<WebElement>> rows = aspectsTable.getRows();
        for (List<WebElement> aspectRow : rows) {
            aspects.add(aspectRow.get(0).getText());
        }

        return aspects;
    }

    public void assertAspectsArePresent(List<String> aspects){
        SoftAssert softAssert= new SoftAssert();
        List<String> aspectsDisplayed = getAspects();
        for(String aspect : aspects){
            System.out.print(aspect);
            softAssert.assertFalse(aspectsDisplayed.indexOf(aspect)==-1,  String.format("[%s] is not displayed in Aspects table", aspect));
        }
        softAssert.assertAll();
    }

    public void assertNodeInformationIs(String nodeInformationName, String expectedNodeInformationValue) throws TestConfigurationException {
        ArrayList<String> actualNodeInformationName = new ArrayList<String>();
        actualNodeInformationName.add("Reference");
        actualNodeInformationName.add("Primary Path");
        actualNodeInformationName.add("Type");
        actualNodeInformationName.add("Parent");

        int nodeRowID = actualNodeInformationName.indexOf(nodeInformationName);
        if (nodeRowID < 0) {
            throw new TestConfigurationException("you did not define all Node Information values in your page object.");
        }
        /*
            0 index based table
            Reference	workspace://SpacesStore/8178a5e1-ad63-40f3-b7ac-a68c1195c3cc
           if nodeRowID = 0 it will return Reference so we need the second column i.e. with index 1 (workspace://SpacesStore/8178a5e1-ad63-40f3-b7ac-a68c1195c3cc)
         */
        String actualNodeInformationValue = nodeInformationTable.getCellAt(nodeRowID, 1).getText();

        Assert.assertEquals(actualNodeInformationValue, expectedNodeInformationValue, "Node Information ");
    }
}
