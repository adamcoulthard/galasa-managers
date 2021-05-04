/*
 * Licensed Materials - Property of IBM
 * 
 * (c) Copyright IBM Corp. 2020,2021.
 */
package dev.galasa.zosfile.zosmf.manager.internal;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.validation.constraints.NotNull;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.service.component.annotations.Component;

import dev.galasa.ManagerException;
import dev.galasa.framework.spi.AbstractManager;
import dev.galasa.framework.spi.AnnotatedField;
import dev.galasa.framework.spi.GenerateAnnotatedField;
import dev.galasa.framework.spi.IFramework;
import dev.galasa.framework.spi.IManager;
import dev.galasa.framework.spi.ResourceUnavailableException;
import dev.galasa.framework.spi.language.GalasaMethod;
import dev.galasa.framework.spi.language.GalasaTest;
import dev.galasa.zos.IZosImage;
import dev.galasa.zos.ZosManagerException;
import dev.galasa.zos.spi.IZosManagerSpi;
import dev.galasa.zosfile.IZosFileHandler;
import dev.galasa.zosfile.ZosFileField;
import dev.galasa.zosfile.ZosFileHandler;
import dev.galasa.zosfile.ZosFileManagerException;
import dev.galasa.zosfile.spi.IZosFileSpi;
import dev.galasa.zosmf.spi.IZosmfManagerSpi;
import dev.galasa.zosunixcommand.spi.IZosUNIXCommandSpi;

/**
 * zOS File Manager implemented using zOS/MF
 *
 */
@Component(service = { IManager.class })
public class ZosmfZosFileManagerImpl extends AbstractManager implements IZosFileSpi {
    protected static final String NAMESPACE = "zosfile";
    
    private static final Log logger = LogFactory.getLog(ZosmfZosFileManagerImpl.class);

    protected IZosManagerSpi zosManager;
    public IZosManagerSpi getZosManager() {
        return this.zosManager;
    }
    
    protected IZosmfManagerSpi zosmfManager;
    public IZosmfManagerSpi getZosmfManager() {
        return this.zosmfManager;
    }

    protected IZosUNIXCommandSpi zosUnixCommandManager;
    public IZosUNIXCommandSpi getZosUnixCommandManager() {
        return this.zosUnixCommandManager;
    }

    private final Map<String, ZosmfZosFileHandlerImpl> zosFileHandlers = new HashMap<>();

    private static final String ZOS_DATASETS = "zOS_Datasets";
    
    private static final String ZOS_VSAM_DATASETS = "zOS_VSAM_Datasets";
    
    private static final String ZOS_UNIX_PATHS = "zOS_Unix_Paths";

    private static final String PROVISIONING = "provisioning";

    private String runId;
    protected void setRunId(String id) {
        this.runId = id;
    }
    protected String getRunId() {
        return this.runId;
    }

    private Path artifactsRoot;
    public Path getArtifactsRoot() {
    	return artifactsRoot;
    }

    protected Path datasetArtifactRoot;
    protected void setDatasetArtifactRoot(Path path) {
        this.datasetArtifactRoot = path;
    }
    protected Path getDatasetArtifactRoot() {
        return this.datasetArtifactRoot;
    }

    protected Path vsamDatasetArtifactRoot;
    protected void setVsamDatasetArtifactRoot(Path path) {
        this.vsamDatasetArtifactRoot = path;
    }
    protected Path getVsamDatasetArtifactRoot() {
        return this.vsamDatasetArtifactRoot;
    }

    protected Path unixPathArtifactRoot;
    protected void setUnixPathArtifactRoot(Path path) {
        this.unixPathArtifactRoot = path;
    }
    protected Path getUnixPathArtifactRoot() {
        return this.unixPathArtifactRoot;
    }
    
    protected String currentTestMethodArchiveFolderName;
    public void setCurrentTestMethodArchiveFolderName(String folderName) {
        this.currentTestMethodArchiveFolderName = folderName;
    }
    public Path getDatasetCurrentTestMethodArchiveFolder() {
        return this.datasetArtifactRoot.resolve(currentTestMethodArchiveFolderName);
    }
    public Path getVsamDatasetCurrentTestMethodArchiveFolder() {
        return this.vsamDatasetArtifactRoot.resolve(currentTestMethodArchiveFolderName);
    }
    public Path getUnixPathCurrentTestMethodArchiveFolder() {
        return this.unixPathArtifactRoot.resolve(currentTestMethodArchiveFolderName);
    }
    
    /* (non-Javadoc)
     * @see dev.galasa.framework.spi.AbstractManager#initialise(dev.galasa.framework.spi.IFramework, java.util.List, java.util.List, java.lang.Class)
     */
    @Override
    public void initialise(@NotNull IFramework framework, @NotNull List<IManager> allManagers,
            @NotNull List<IManager> activeManagers, @NotNull GalasaTest galasaTest) throws ManagerException {
        super.initialise(framework, allManagers, activeManagers, galasaTest);

        if(galasaTest.isJava()) {
            //*** Check to see if any of our annotations are present in the test class
            //*** If there is,  we need to activate
            List<AnnotatedField> ourFields = findAnnotatedFields(ZosFileField.class);
            if (!ourFields.isEmpty()) {
                youAreRequired(allManagers, activeManagers, galasaTest);
            }
        }
        
        setRunId(getFramework().getTestRunName());
        
        artifactsRoot = getFramework().getResultArchiveStore().getStoredArtifactsRoot();
        
        setDatasetArtifactRoot(artifactsRoot.resolve(ZOS_DATASETS));        
        setVsamDatasetArtifactRoot(artifactsRoot.resolve(ZOS_VSAM_DATASETS));        
        setUnixPathArtifactRoot(artifactsRoot.resolve(ZOS_UNIX_PATHS));
        this.currentTestMethodArchiveFolderName = "preTest";
    }
        
    
    /* (non-Javadoc)
     * @see dev.galasa.framework.spi.AbstractManager#provisionGenerate()
     */
    @Override
    public void provisionGenerate() throws ManagerException, ResourceUnavailableException {
        generateAnnotatedFields(ZosFileField.class);
    }


    /* (non-Javadoc)
     * @see dev.galasa.framework.spi.AbstractManager#youAreRequired()
     */
    @Override
    public void youAreRequired(@NotNull List<IManager> allManagers, @NotNull List<IManager> activeManagers, @NotNull GalasaTest galasaTest)
            throws ManagerException {
        if (activeManagers.contains(this)) {
            return;
        }

        activeManagers.add(this);
        this.zosManager = addDependentManager(allManagers, activeManagers, galasaTest, IZosManagerSpi.class);
        if (zosManager == null) {
            throw new ZosFileManagerException("The zOS Manager is not available");
        }
        this.zosmfManager = addDependentManager(allManagers, activeManagers, galasaTest, IZosmfManagerSpi.class);
        if (zosmfManager == null) {
            throw new ZosFileManagerException("The zOSMF Manager is not available");
        }
        this.zosUnixCommandManager = addDependentManager(allManagers, activeManagers, galasaTest, IZosUNIXCommandSpi.class);
        if (zosUnixCommandManager == null) {
            throw new ZosFileManagerException("The zOS UNIX Command Manager is not available");
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see dev.galasa.framework.spi.IManager#areYouProvisionalDependentOn(dev.galasa.framework.spi.IManager)
     */
    @Override
    public boolean areYouProvisionalDependentOn(@NotNull IManager otherManager) {
        return otherManager instanceof IZosManagerSpi ||
                otherManager instanceof IZosmfManagerSpi ||
                otherManager instanceof IZosUNIXCommandSpi;
    }

    /*
     * (non-Javadoc)
     * 
     * @see dev.galasa.framework.spi.IManager#provisionBuild()
     */
    @Override
    public void provisionBuild() throws ManagerException, ResourceUnavailableException {
        setDatasetArtifactRoot(artifactsRoot.resolve(PROVISIONING).resolve(ZOS_DATASETS));        
        setVsamDatasetArtifactRoot(artifactsRoot.resolve(PROVISIONING).resolve(ZOS_VSAM_DATASETS));        
        setUnixPathArtifactRoot(artifactsRoot.resolve(PROVISIONING).resolve(ZOS_UNIX_PATHS));
        setCurrentTestMethodArchiveFolderName("preTest");
    }

    /*
     * (non-Javadoc)
     * 
     * @see dev.galasa.framework.spi.IManager#startOfTestMethod()
     */
    @Override
    public void startOfTestMethod(@NotNull GalasaMethod galasaMethod) throws ManagerException {
        setDatasetArtifactRoot(artifactsRoot.resolve(ZOS_DATASETS));        
        setVsamDatasetArtifactRoot(artifactsRoot.resolve(ZOS_VSAM_DATASETS));        
        setUnixPathArtifactRoot(artifactsRoot.resolve(ZOS_UNIX_PATHS));
        if (galasaMethod.getJavaTestMethod() != null) {
            setCurrentTestMethodArchiveFolderName(galasaMethod.getJavaTestMethod().getName() + "." + galasaMethod.getJavaExecutionMethod().getName());
        } else {
            setCurrentTestMethodArchiveFolderName(galasaMethod.getJavaExecutionMethod().getName());
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see dev.galasa.framework.spi.IManager#endOfTestClass(java.lang.String,java.lang.Throwable)
     */
    @Override
    public String endOfTestClass(@NotNull String currentResult, Throwable currentException) throws ManagerException {
        setDatasetArtifactRoot(artifactsRoot.resolve(PROVISIONING).resolve(ZOS_DATASETS));        
        setVsamDatasetArtifactRoot(artifactsRoot.resolve(PROVISIONING).resolve(ZOS_VSAM_DATASETS));        
        setUnixPathArtifactRoot(artifactsRoot.resolve(PROVISIONING).resolve(ZOS_UNIX_PATHS));
        setCurrentTestMethodArchiveFolderName("postTest");
        
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see dev.galasa.framework.spi.IManager#endOfTestRun()
     */
    @Override
    public void endOfTestRun() {
        try {
            cleanup();
        } catch (ZosFileManagerException e) {
            logger.error("Problem in endOfTestRun()", e);
        }
    }
    
    protected void cleanup() throws ZosFileManagerException {
        for (Entry<String, ZosmfZosFileHandlerImpl> entry : zosFileHandlers.entrySet()) {
            entry.getValue().cleanup();
        }
    }
    
    @GenerateAnnotatedField(annotation=ZosFileHandler.class)
    public IZosFileHandler generateZosFileHandler(Field field, List<Annotation> annotations) {
        ZosmfZosFileHandlerImpl zosmfZosFileHandlerImpl = new ZosmfZosFileHandlerImpl(this, field.getName());
        zosFileHandlers.put(zosmfZosFileHandlerImpl.toString(), zosmfZosFileHandlerImpl);        
        return zosmfZosFileHandlerImpl;
    }
    
    public IZosFileHandler newZosFileHandler() {
        ZosmfZosFileHandlerImpl zosmfZosFileHandlerImpl;
        if (zosFileHandlers.get("INTERNAL") == null) {
            zosmfZosFileHandlerImpl = new ZosmfZosFileHandlerImpl(this);
            zosFileHandlers.put(zosmfZosFileHandlerImpl.toString(), zosmfZosFileHandlerImpl);
        }
        return zosFileHandlers.get("INTERNAL");
    }
    
    public String getRunDatasetHLQ(IZosImage image) throws ZosFileManagerException {
        try {
            return zosManager.getRunDatasetHLQ(image);
        } catch (ZosManagerException e) {
            throw new ZosFileManagerException(e);
        }
    }
    
    @Override
    public @NotNull IZosFileHandler getZosFileHandler() throws ZosFileManagerException {
        return newZosFileHandler();
    }
}
