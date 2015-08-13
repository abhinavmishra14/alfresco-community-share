/*
 * Copyright (C) 2005-2012 Alfresco Software Limited.
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
package org.alfresco.po.share.util;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.imageio.ImageIO;

import org.alfresco.dataprep.SiteService;
import org.alfresco.po.exception.PageException;
import org.alfresco.po.share.FactoryPage;
import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.site.SiteFinderPage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.social.alfresco.api.entities.Site.Visibility;
import org.springframework.stereotype.Component;
import org.testng.SkipException;
@Component
/**
 * Utility class to manage site related operations
 * <ul>
 * <li>Creates site by calling REST API.</li>
 * <li>Deletes site by calling REST API.</li>
 * <li>Gets NodeRef value by site name.</li>
 * </ul>
 *
 * @author Michael Suzuki
 * @author Shan Nagarajan
 * @since 1.0
 */
public class SiteUtil
{
    private final static  Log logger = LogFactory.getLog(SiteUtil.class);
    private final  String ERROR_MESSAGE_PATTERN = "Failed to create a new site %n Site Name: %s%n Create Site API URL: %s%n";
    @Autowired  FactoryPage factoryPage;
    @Autowired SiteService siteService;
    @Value("${share.target}") String shareUrl;
    /**
     * Prepare a file in system temp directory to be used
     * in test for uploads.
     *
     * @return {@link File} simple text file.
     */
    public File prepareFile()
    {
        return prepareFile(null);
    }

    /**
     * Prepare a plain text file in system temp directory to be used
     * in test for uploads.
     *
     * @param name Name to give the file, without the file extension. If null a default name will be used.
     * @param data Content to write to the file
     */
    public File prepareFile(final String name, String data)
    {
        return prepareFile(name, data, ".txt");
    }

    /**
     * Prepare a file in system temp directory to be used
     * in test for uploads.
     *
     * @param name      Name to give the file, without the file extension. If null a default name will be used.
     * @param data      Content to write to the file
     * @param extension File extension to append to the end of the filename
     * @return {@link File} simple text file.
     */
    public File prepareFile(final String name, String data, String extension)
    {

        File file = null;
        OutputStreamWriter writer = null;
        try
        {
            String fileName = (name != null && !name.isEmpty() ? name : "myfile");
            file = File.createTempFile(fileName, extension);

            writer = new OutputStreamWriter(new FileOutputStream(file));
            writer.write(data);
            writer.close();
        }
        catch (IOException ioe)
        {
            logger.error("Unable to create sample file", ioe);
        }
        catch (Exception e)
        {
            logger.error("Unable to create site", e);
        }
        finally
        {
            if (writer != null)
            {
                try
                {
                    writer.close();
                }
                catch (IOException ioe)
                {
                    logger.error("Unable to close properly", ioe);
                }
            }
        }
        return file;
    }
    
    
    /**
     * Prepare a zip or acp file in system temp directory to be used
     * in test for uploads.
     *
     * @param name      Name to give the file, without the file extension. If null a default name will be used.
     * @param extension File extension to append to the end of the filename
     * @return {@link File} simple zip/acp file.
     */
    public static File prepareZipFile(final String name, String extension)
    {

        File file = null;
        OutputStreamWriter writer = null;
        try
        {
            String fileName = (name != null && !name.isEmpty() ? name : "myfile");
            file = File.createTempFile(fileName, extension);
            
            ZipOutputStream out = new ZipOutputStream(new FileOutputStream(file));
            ZipEntry e = new ZipEntry(fileName + ".txt");
            out.putNextEntry(e);
            
            writer = new OutputStreamWriter(out);
            writer.close();
        }
        catch (IOException ioe)
        {
            logger.error("Unable to create sample file", ioe);
        }
        catch (Exception e)
        {
            logger.error("Unable to create site", e);
        }
        finally
        {
            if (writer != null)
            {
                try
                {
                    writer.close();
                }
                catch (IOException ioe)
                {
                    logger.error("Unable to close properly", ioe);
                }
            }
        }
        return file;
    }
    
    

    /**
     * Prepare a text file in system temp directory to be used
     * in test for uploads, containing default content.
     *
     * @param name Name to give the file, without the file extension. If null a default name will be used.
     * @return {@link File} simple text file.
     */
    public File prepareFile(final String name)
    {
        return prepareFile(name, "this is a sample test upload file");

    }

    /**
     * Create site using share
     *
     * @param String username
     * @param String password
     * @param siteName      String site name
     * @param desc String
     * @param siteVisibility SiteVisiblity
     * @return true if site created
     */
    public void createSite(final WebDriver driver, final String username, final String password, final String siteName, String desc, String siteVisibility)
    {
        if (siteName == null || siteName.isEmpty())
            throw new UnsupportedOperationException("site name is required");
		try 
		{
			siteService.create(username, password, "testdomain", siteName, desc, Visibility.valueOf(siteVisibility.toUpperCase()));
		} 
		catch (IOException e) 
		{
			throw new RuntimeException("Unable to create site " + siteName, e);
		}
        driver.navigate().to(shareUrl+"/page/site/" + siteName + "/dashboard");
    }

    /**
     * Deletes site using share
     * @param String username
     * @param String password
     * @param siteName String site name
     * @return true if site deleted
     */
    public void deleteSite(final String username, final String password, final String siteName)
    {
		siteService.delete(username, password, "testdomain", siteName);
    }

    /**
     * Search site using share.
     *
     * @param siteName String site name
     * @return site name
     */
    public SiteFinderPage searchSite(WebDriver driver, final String siteName)
    {

        if (siteName == null || siteName.isEmpty())
            throw new UnsupportedOperationException("site name is required");
        try
        {
            SharePage page = factoryPage.getPage(driver).render();
            SiteFinderPage siteFinder = page.getNav().selectSearchForSites().render();
            siteFinder = siteFinder.searchForSite(siteName).render();
            return siteFinder;
        }
        catch (UnsupportedOperationException une)
        {
            String msg = String.format(ERROR_MESSAGE_PATTERN, siteName);
            throw new RuntimeException(msg, une);
        }
        catch (NoSuchElementException nse)
        {
            logger.error("Site not found!");
        }

        throw new PageException("Page is not found!!");
    }

    /**
     * This method create in Temp directory jpg file for uploading.
     *
     * @param jpgName String
     * @return File object for created Image.
     */
    public File prepareJpg(String jpgName)
    {
        BufferedImage image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();
        g.drawString("Test Publish file.", 5, 10);
        g.drawString(jpgName, 5, 50);
        try
        {
            File jpgFile = File.createTempFile(jpgName, ".jpg");
            ImageIO.write(image, "jpg", jpgFile);
            return jpgFile;
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        throw new SkipException("Can't create JPG file");
    }
    
    /**
     * 
     * Searching with retry for sites to handle solr lag
     * 
     * @param drone WebDrone
     * @param finderPage SiteFinderPage
     * @param siteName String
     * @return SiteFinderPage
     */
    public SiteFinderPage siteSearchRetry(WebDriver driver, SiteFinderPage finderPage, String siteName)
    {
        int counter = 0;
        int waitInMilliSeconds = 2000;
        int retrySearchCount = 5;
        while(counter < retrySearchCount)
        {
            SiteFinderPage siteSearchResults = finderPage.searchForSite(siteName).render();
            if(siteSearchResults.getSiteList().contains(siteName))
            {
                return siteSearchResults;
            }
            else
            {
                counter++;
                factoryPage.getPage(driver).render();
            }
            //double wait time to not over do solr search
            waitInMilliSeconds = (waitInMilliSeconds*2);
            synchronized (SiteUtil.class)
            {
                try{ SiteUtil.class.wait(waitInMilliSeconds); } catch (InterruptedException e) {}
            }
        }
        throw new PageException("site search failed");
    }
}
