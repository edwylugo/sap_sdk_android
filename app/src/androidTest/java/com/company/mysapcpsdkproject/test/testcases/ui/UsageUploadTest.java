package com.company.mysapcpsdkproject.test.testcases.ui;

import androidx.test.rule.ActivityTestRule;
import static androidx.test.InstrumentationRegistry.getInstrumentation;

import com.company.mysapcpsdkproject.app.SAPWizardApplication;
import com.company.mysapcpsdkproject.logon.LogonActivity;
import com.company.mysapcpsdkproject.test.core.BaseTest;
import com.company.mysapcpsdkproject.test.core.Constants;
import com.company.mysapcpsdkproject.test.core.Credentials;
import com.company.mysapcpsdkproject.test.core.Utils;
import com.company.mysapcpsdkproject.test.core.WizardDevice;
import com.company.mysapcpsdkproject.test.pages.DetailPage;
import com.company.mysapcpsdkproject.test.pages.EntityListPage;
import com.company.mysapcpsdkproject.test.pages.MasterPage;
import com.company.mysapcpsdkproject.test.pages.PasscodePage;
import com.company.mysapcpsdkproject.test.pages.SettingsListPage;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class UsageUploadTest extends BaseTest {

    @Rule
    public ActivityTestRule<LogonActivity> activityTestRule = new ActivityTestRule<>(LogonActivity.class);

    /**
     * Basic usageupload flow
     */
    @Test
    public void testUsageUploadBackgroundLocked() {
        Constants.USAGE_CONSENT = Constants.UsageConsent.ALLOW;
        // This test just tests whether the buttons works as expected
        // no crash and the toast appears or not
        Utils.doOnboarding();

        EntityListPage entityListPage = new EntityListPage();
        entityListPage.clickFirstElement();
        entityListPage.leavePage();

        MasterPage masterPage = new MasterPage();
        masterPage.clickFirstElement();
        masterPage.leavePage();

        DetailPage detailPage = new DetailPage();
        detailPage.clickBack();
        detailPage.leavePage();

        masterPage = new MasterPage();
        masterPage.clickBack();
        masterPage.leavePage();

        entityListPage = new EntityListPage();
        entityListPage.clickSettings();
        entityListPage.leavePage();

        SettingsListPage settingsListPage = new SettingsListPage();

        // Put the application into background and wait until the app is locked
        int lockTimeOut = ((SAPWizardApplication) getInstrumentation().getTargetContext().getApplicationContext())
                .getSecureStoreManager().getPasscodeLockTimeout();
        WizardDevice.putApplicationBackground((lockTimeOut + 1) * 1000, activityTestRule);
        // Reopen app
        WizardDevice.reopenApplication();

        PasscodePage.EnterPasscodePage enterPasscodePage = new PasscodePage().new EnterPasscodePage();
        enterPasscodePage.enterPasscode(Credentials.PASSCODE);
        enterPasscodePage.clickSignIn();
        enterPasscodePage.leavePage();

        settingsListPage.clickUploadUsage();
        settingsListPage.checkUsageUploadToast();
    }

    @Before
    public void usageSetUp() {
    }

    @After
    public void usageTearDown() {
        Constants.USAGE_CONSENT = Constants.UsageConsent.DENY;
    }

}
