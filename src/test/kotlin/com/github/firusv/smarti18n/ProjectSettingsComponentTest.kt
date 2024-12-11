package com.github.firusv.smarti18n.settings

import com.intellij.openapi.vfs.VirtualFile
import com.intellij.testFramework.TestDataPath
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import javax.swing.DefaultListModel

@TestDataPath("src/test/testData/settings")
class ProjectSettingsComponentTest : BasePlatformTestCase() {

    fun testInitialFileListIsEmpty() {
        val component = ProjectSettingsComponent(project)
        val fileList = component.fileList
        val fileModel = fileList.model as DefaultListModel<VirtualFile>

        assertNotNull(fileList)
        assertEquals(0, fileModel.size())
    }

    fun testAddValidJsonFile() {
        val component = ProjectSettingsComponent(project)
        val fileList = component.fileList
        val fileModel = fileList.model as DefaultListModel<VirtualFile>
        val mockFile = myFixture.tempDirFixture.createFile("test.json")

        component.addFileToListModel(mockFile)

        assertEquals(1, fileModel.size())
        assertTrue(fileModel.contains(mockFile))
    }

    fun testPreventDuplicateFiles() {
        val component = ProjectSettingsComponent(project)
        val fileList = component.fileList
        val fileModel = fileList.model as DefaultListModel<VirtualFile>
        val mockFile = myFixture.tempDirFixture.createFile("duplicate.json")

        component.addFileToListModel(mockFile)
        component.addFileToListModel(mockFile)

        assertEquals(1, fileModel.size())
    }

    fun testRemoveSelectedFile() {
        val component = ProjectSettingsComponent(project)
        val fileList = component.fileList
        val fileModel = fileList.model as DefaultListModel<VirtualFile>
        val mockFile = myFixture.tempDirFixture.createFile("toRemove.json")

        component.addFileToListModel(mockFile)
        fileList.selectedIndex = 0

        component.removeSelectedFileFromFileListModel()

        assertEquals(0, fileModel.size())
        assertFalse(fileModel.contains(mockFile))
    }

    fun testUpdateDefaultLangOnFileListChange() {
        val component = ProjectSettingsComponent(project)
        val defaultLang = component.defaultLang
        val mockFile = myFixture.tempDirFixture.createFile("en.json")

        component.addFileToListModel(mockFile)

        assertEquals(1, defaultLang.itemCount)
        assertEquals("en", defaultLang.selectedItem)
    }

    fun testDisableRemoveButtonByDefault(){
        val component = ProjectSettingsComponent(project)
        val fileList = component.fileList
        val removeButton = component.removeButton
        val mockFile = myFixture.tempDirFixture.createFile("en.json")

        assertEquals(false, removeButton.isEnabled)
        component.addFileToListModel(mockFile)
        assertEquals(false, removeButton.isEnabled)
        fileList.selectedIndex = 0
        assertEquals(true, removeButton.isEnabled)
        component.removeSelectedFileFromFileListModel()
        assertEquals(false, removeButton.isEnabled)
    }

    override fun getTestDataPath(): String = "src/test/testData/settings"
}
