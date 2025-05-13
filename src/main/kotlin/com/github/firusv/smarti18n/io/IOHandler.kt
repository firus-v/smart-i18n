package com.github.firusv.smarti18n.io


import com.github.firusv.smarti18n.exception.EmptyLocalesDirException
import com.github.firusv.smarti18n.exception.SyntaxException
import com.github.firusv.smarti18n.io.folder.FolderStrategy
import com.github.firusv.smarti18n.io.parser.ParserStrategy
import com.github.firusv.smarti18n.io.parser.ParserStrategyType
import com.github.firusv.smarti18n.model.TranslationData
import com.github.firusv.smarti18n.settings.ProjectSettingsState
import com.github.firusv.smarti18n.util.NotificationHelper
import com.intellij.codeInsight.actions.ReformatCodeProcessor
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiFile
import java.io.IOException

/**
 * Центральный компонент для операций ввода-вывода на основе настроенных стратегий.
 * @author firus-v
 */
class IOHandler(
    private val project: Project,
    private val settings: ProjectSettingsState
) {

    private val folderStrategy: FolderStrategy
    private val parserStrategyType: ParserStrategyType = ParserStrategyType.JSON
    private val parserStrategy: ParserStrategy

    init {
        // Инициализация стратегий
        this.folderStrategy = FolderStrategy()

        this.parserStrategy = parserStrategyType.getStrategy()
            .getDeclaredConstructor(ProjectSettingsState::class.java)
            .newInstance(settings)
    }

    /**
     * Читает файлы переводов из локального проекта в нашу структуру данных.
     * <b>Примечание:</b> Этот метод должен быть вызван в контексте Read-Action (см. ApplicationManager)
     * @return Данные перевода, основанные на настроенных стратегиях
     * @throws IOException Ошибка при чтении данных перевода
     */
    @Throws(IOException::class)
    fun read(): TranslationData {
        val fileList = settings.getFileListModel();

        if (fileList.size == 0) {
            throw EmptyLocalesDirException("File list must not be empty")
        }

        // TODO сортировка переводов по алфавиту settings.isSorting
        val data = TranslationData(false)
        val translationFiles = folderStrategy.analyzeFileList(fileList)

        for (file in translationFiles) {
            try {
                parserStrategy.read(file, data)
            } catch (ex: SyntaxException) {
                NotificationHelper.createBadSyntaxNotification(project, ex)
            } catch (ex: Exception) {
                throw IOException("$file\n\n${ex.message}", ex)
            }
        }

        return data
    }

    /**
     * Записывает переданные данные перевода в локальные файлы проекта.
     * <b>Примечание:</b> Этот метод должен быть вызван в контексте Write-Action (см. ApplicationManager)
     * @param data Закэшированные данные перевода для сохранения
     * @throws IOException Ошибка при записи данных
     */
    @Throws(IOException::class)
    fun write(data: TranslationData) {
        val fileListModel = settings.getFileListModel()

        if (fileListModel.size == 0) {
            throw EmptyLocalesDirException("File list must not be empty")
        }

        val translationFiles = folderStrategy.analyzeFileList(fileListModel)

        for (file in translationFiles) {
            try {
                val content = parserStrategy.write(data, file)

                if (content == null) {
                    // Если в целевом файле нет контента, его нужно удалить
                    continue
                }

                val document = FileDocumentManager.getInstance().getDocument(file.virtualFile)
                    ?: throw IOException("Failed to retrieve document for $file")

                // Контент должен использовать символы новой строки \n (внутреннее руководство IntelliJ)
                document.setText(content)

                var psi: PsiFile? = PsiDocumentManager.getInstance(project).getCachedPsiFile(document)

                if (psi == null) {
                    psi = PsiDocumentManager.getInstance(project).getPsiFile(document)
                }

                requireNotNull(psi)

                // Форматируем код
                ReformatCodeProcessor(psi, false).run()

                // Сохраняем документ
                FileDocumentManager.getInstance().saveDocument(document)

            } catch (ex: Exception) {
                throw IOException("$file\n\n${ex.message}", ex)
            }
        }
    }
}