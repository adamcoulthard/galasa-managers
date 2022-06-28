/*
 * Copyright contributors to the Galasa project
 */
package dev.galasa.zosconsole.internal.properties;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
//import org.powermock.api.mockito.PowerMockito;
//import org.powermock.core.classloader.annotations.PrepareForTest;
//import org.powermock.modules.junit4.PowerMockRunner;

import dev.galasa.framework.spi.ConfigurationPropertyStoreException;
import dev.galasa.framework.spi.IConfigurationPropertyStoreService;
import dev.galasa.framework.spi.cps.CpsProperties;
import dev.galasa.zosconsole.ZosConsoleManagerException;

//@RunWith(PowerMockRunner.class)
//@PrepareForTest({ZosConsolePropertiesSingleton.class, CpsProperties.class})
public class TestRestrictToImage {
//    
//    @Mock
//    private IConfigurationPropertyStoreService configurationPropertyStoreServiceMock;
//    
//    private static final String IMAGE_ID = "IMAGE";
//    
//    @Test
//    public void testConstructor() {
//        ConsoleRestrictToImage consoleRestrictToImage = new ConsoleRestrictToImage();
//        Assert.assertNotNull("Object was not created", consoleRestrictToImage);
//    }
//    
//    @Test
//    public void testNull() throws Exception {
//        Assert.assertEquals("Unexpected value returned from ConsoleRestrictToImage.get()", false, getProperty(null));
//    }
//    
//    @Test
//    public void testValid() throws Exception {
//        Assert.assertEquals("Unexpected value returned from ConsoleRestrictToImage.get()", true, getProperty("true"));
//        Assert.assertEquals("Unexpected value returned from ConsoleRestrictToImage.get()", true, getProperty("TRUE"));
//        Assert.assertEquals("Unexpected value returned from ConsoleRestrictToImage.get()", true, getProperty("TrUe"));
//        Assert.assertEquals("Unexpected value returned from ConsoleRestrictToImage.get()", false, getProperty("fasle"));
//        Assert.assertEquals("Unexpected value returned from ConsoleRestrictToImage.get()", false, getProperty("FALSE"));
//        Assert.assertEquals("Unexpected value returned from ConsoleRestrictToImage.get()", false, getProperty("FaLsE"));
//    }
//    
//    @Test
//    public void testInvalid() throws Exception {
//        Assert.assertEquals("Unexpected value returned from ConsoleRestrictToImage.get()", false, getProperty("XXX"));
//        Assert.assertEquals("Unexpected value returned from ConsoleRestrictToImage.get()", false, getProperty("999"));
//    }
//    
//    @Test
//    public void testException() throws Exception {
//        String expectedMessage = "Problem asking the CPS for the console restrict to image property for zOS image " + IMAGE_ID;
//        ZosConsoleManagerException expectedException = Assert.assertThrows("expected exception should be thrown", ZosConsoleManagerException.class, ()->{
//        	getProperty("ANY", true);
//        });
//        Assert.assertEquals("exception should contain expected cause", expectedMessage, expectedException.getMessage());
//    }
//
//    private boolean getProperty(String value) throws Exception {
//        return getProperty(value, false);
//    }
//    
//    private boolean getProperty(String value, boolean exception) throws Exception {
//        PowerMockito.spy(ZosConsolePropertiesSingleton.class);
//        PowerMockito.doReturn(configurationPropertyStoreServiceMock).when(ZosConsolePropertiesSingleton.class, "cps");
//        PowerMockito.spy(CpsProperties.class);
//        
//        if (!exception) {
//            PowerMockito.doReturn(value).when(CpsProperties.class, "getStringNulled", Mockito.any(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString());            
//        } else {
//            PowerMockito.doThrow(new ConfigurationPropertyStoreException()).when(CpsProperties.class, "getStringNulled", Mockito.any(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
//        }
//        
//        return ConsoleRestrictToImage.get(IMAGE_ID);
//    }
}
