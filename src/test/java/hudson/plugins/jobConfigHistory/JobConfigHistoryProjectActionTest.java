package hudson.plugins.jobConfigHistory;

import hudson.XmlFile;
import hudson.maven.MavenModule;
import hudson.model.AbstractItem;
import hudson.model.AbstractProject;
import hudson.model.Hudson;
import hudson.model.ItemGroup;
import java.io.File;
import java.util.List;
import org.acegisecurity.AccessDeniedException;
import org.hamcrest.CoreMatchers;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;
import org.junit.Rule;
import static org.mockito.Mockito.*;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

/**
 *
 * @author Mirko Friedenhagen
 */
public class JobConfigHistoryProjectActionTest {

    @Rule
    public UnpackResourceZip testConfigs = new UnpackResourceZip(JobConfigHistoryProjectActionTest.class.getResource("JobConfigHistoryPurgerIT.zip"));

    private final ItemGroup mockedItemGroup = mock(ItemGroup.class);
    private final MavenModule mockedMavenModule = mock(MavenModule.class);
    private final JobConfigHistory mockedPlugin = mock(JobConfigHistory.class);
    private final Hudson mockedHudson = mock(hudson.model.Hudson.class);
    private final AbstractItem mockedProject = mock(AbstractItem.class);
    private final StaplerRequest mockedRequest = mock(StaplerRequest.class);
    //private final StaplerResponse mockedResponse = mock(StaplerResponse.class);


    public JobConfigHistoryProjectActionTest() {
        when(mockedItemGroup.getFullName()).thenReturn("");
        when(mockedProject.getParent()).thenReturn(mockedItemGroup);
        when(mockedProject.getFullName()).thenReturn("Test1");
    }

    /**
     * Test of getIconFileName method, of class JobConfigHistoryProjectAction.
     */
    @Test
    public void testGetIconFileNameNoPermission() {
        when(mockedProject.hasPermission(AbstractProject.CONFIGURE)).thenReturn(false);
        JobConfigHistoryProjectAction sut = createAction();
        assertNull(sut.getIconFileName());
    }

    /**
     * Test of getIconFileName method, of class JobConfigHistoryProjectAction.
     */
    @Test
    public void testGetIconFileNameSaveProjectNonMavenModules() {
        when(mockedProject.hasPermission(AbstractProject.CONFIGURE)).thenReturn(true);
        when(mockedPlugin.getSaveModuleConfiguration()).thenReturn(false);
        JobConfigHistoryProjectAction sut = createAction();
        assertEquals(JobConfigHistoryConsts.ICONFILENAME, sut.getIconFileName());
    }

    /**
     * Test of getIconFileName method, of class JobConfigHistoryProjectAction.
     */
    @Test
    public void testGetIconFileNameSaveMavenModules() {
        when(mockedMavenModule.hasPermission(AbstractProject.CONFIGURE)).thenReturn(true);
        when(mockedPlugin.getSaveModuleConfiguration()).thenReturn(true);
        JobConfigHistoryProjectAction sut = createActionForMavenModule();
        assertEquals(JobConfigHistoryConsts.ICONFILENAME, sut.getIconFileName());
    }

    /**
     * Test of getIconFileName method, of class JobConfigHistoryProjectAction.
     */
    @Test
    public void testGetIconFileNameDoNotSaveMavenModules() {
        when(mockedMavenModule.hasPermission(AbstractProject.CONFIGURE)).thenReturn(true);
        when(mockedPlugin.getSaveModuleConfiguration()).thenReturn(false);
        JobConfigHistoryProjectAction sut = createActionForMavenModule();
        assertNull(sut.getIconFileName());
    }

    /**
     * Test of getJobConfigs method, of class JobConfigHistoryProjectAction.
     */
    @Test
    public void testGetJobConfigs() throws Exception {
        when(mockedProject.hasPermission(AbstractProject.CONFIGURE)).thenReturn(true);
        when(mockedPlugin.getHistoryDir(any(XmlFile.class))).thenReturn(
                testConfigs.getResource("config-history/jobs/Test1"));
        final JobConfigHistoryProjectAction sut = createAction();
        final List<ConfigInfo> result = sut.getJobConfigs();
        assertEquals(5, result.size());
    }

    /**
     * Test of getJobConfigs method, of class JobConfigHistoryProjectAction.
     */
    @Test
    public void testGetJobConfigsEmpty() throws Exception {
        when(mockedProject.hasPermission(AbstractProject.CONFIGURE)).thenReturn(true);
        when(mockedPlugin.getHistoryDir(any(XmlFile.class))).thenReturn(
                testConfigs.getResource("config-history/jobs/I_DO_NOT_EXIST"));
        final JobConfigHistoryProjectAction sut = createAction();
        final List<ConfigInfo> result = sut.getJobConfigs();
        assertEquals(0, result.size());
    }

    /**
     * Test of getFile method, of class JobConfigHistoryProjectAction.
     */
    @Test
    public void testGetFile() throws Exception {
        when(mockedProject.hasPermission(AbstractProject.CONFIGURE)).thenReturn(true);
        when(mockedPlugin.getJobHistoryRootDir()).thenReturn(
                testConfigs.getResource("config-history/jobs"));
        when(mockedPlugin.getConfigFile(any(File.class))).thenReturn(
                testConfigs.getResource("config-history/jobs/Test1/2012-11-21_11-40-28/config.xml"));
        when(mockedRequest.getParameter("timestamp")).thenReturn("2012-11-21_11-40-28");
        final JobConfigHistoryProjectAction sut = createAction();
        String result = sut.getFile();
        assertThat(result, CoreMatchers.startsWith("<?xml version="));
        assertThat(result, CoreMatchers.endsWith("</project>"));
    }

    /**
     * Test of getProject method, of class JobConfigHistoryProjectAction.
     */
    @Test
    public void testGetProject() {
        JobConfigHistoryProjectAction sut = createAction();
        assertEquals(mockedProject, sut.getProject());
    }

    /**
     * Test of getAccessControlledObject method, of class JobConfigHistoryProjectAction.
     */
    @Test
    public void testGetAccessControlledObject() {
        JobConfigHistoryProjectAction sut = createAction();
        assertEquals(mockedProject, sut.getAccessControlledObject());
    }

    /**
     * Test of checkConfigurePermission method, of class JobConfigHistoryProjectAction.
     */
    @Test(expected = AccessDeniedException.class)
    public void testCheckConfigurePermission() {
        doThrow(new AccessDeniedException("Oops")).when(mockedProject).checkPermission(AbstractItem.CONFIGURE);
        JobConfigHistoryProjectAction sut = createAction();
        sut.checkConfigurePermission();
    }

    /**
     * Test of hasConfigurePermission method, of class JobConfigHistoryProjectAction.
     */
    @Test
    public void testHasConfigurePermission() {
        when(mockedProject.hasPermission(AbstractProject.CONFIGURE)).thenReturn(true);
        JobConfigHistoryProjectAction sut = createAction();
        assertTrue(sut.hasConfigurePermission());
    }

    /**
     * Test of hasConfigurePermission method, of class JobConfigHistoryProjectAction.
     */
    @Test
    public void testHasNoConfigurePermission() {
        when(mockedProject.hasPermission(AbstractProject.CONFIGURE)).thenReturn(false);
        JobConfigHistoryProjectAction sut = createAction();
        assertFalse(sut.hasConfigurePermission());
    }

    /**
     * Test of doDiffFiles method, of class JobConfigHistoryProjectAction.
     */
    @Test
    @Ignore
    public void testDoDiffFiles() throws Exception {
        System.out.println("doDiffFiles");
        StaplerRequest req = null;
        StaplerResponse rsp = null;
        JobConfigHistoryProjectAction sut = null;
        sut.doDiffFiles(req, rsp);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getLines method, of class JobConfigHistoryProjectAction.
     */
    @Test
    @Ignore
    public void testGetLines() throws Exception {
        System.out.println("getLines");
        JobConfigHistoryProjectAction sut = null;
        List<JobConfigHistoryBaseAction.SideBySideView.Line> expResult = null;
        List<JobConfigHistoryBaseAction.SideBySideView.Line> result = sut.getLines();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of doRestore method, of class JobConfigHistoryProjectAction.
     */
    @Test
    @Ignore
    public void testDoRestore() throws Exception {
        System.out.println("doRestore");
        StaplerRequest req = null;
        StaplerResponse rsp = null;
        JobConfigHistoryProjectAction sut = null;
        sut.doRestore(req, rsp);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of doForwardToRestoreQuestion method, of class JobConfigHistoryProjectAction.
     */
    @Test
    @Ignore
    public void testDoForwardToRestoreQuestion() throws Exception {
        System.out.println("doForwardToRestoreQuestion");
        StaplerRequest req = null;
        StaplerResponse rsp = null;
        JobConfigHistoryProjectAction sut = null;
        sut.doForwardToRestoreQuestion(req, rsp);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    private JobConfigHistoryProjectAction createAction() {
        when(mockedHudson.getPlugin(JobConfigHistory.class)).thenReturn(mockedPlugin);
        return new JobConfigHistoryProjectActionImpl(mockedHudson, mockedProject);
    }

    private JobConfigHistoryProjectAction createActionForMavenModule() {
        when(mockedHudson.getPlugin(JobConfigHistory.class)).thenReturn(mockedPlugin);
        return new JobConfigHistoryProjectActionImpl(mockedHudson, mockedMavenModule);
    }

    private class JobConfigHistoryProjectActionImpl extends JobConfigHistoryProjectAction {

        public JobConfigHistoryProjectActionImpl(Hudson hudson, AbstractItem project) {
            super(hudson, project);
        }

        @Override
        StaplerRequest getCurrentRequest() {
            return mockedRequest;
        }
    }
}