package com.github.firusv.smarti18n.io.parser
import com.github.firusv.smarti18n.model.KeyPath
import com.github.firusv.smarti18n.model.TranslationData
import com.github.firusv.smarti18n.model.TranslationFile
import com.github.firusv.smarti18n.model.TranslationNode
import com.github.firusv.smarti18n.settings.ProjectSettingsState
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable

/**
 * Представляет парсер для определенного формата файла.
 * @author firus-v
 */
abstract class ParserStrategy(protected val settings: @NotNull ProjectSettingsState) {

    /**
     * Читает файл перевода в объект данных перевода (с учетом пространства имен и локали).
     * @param file Файл для чтения
     * @param data Целевой объект данных перевода для сохранения разобранных данных
     */
    @Throws(Exception::class)
    abstract fun read(file: @NotNull TranslationFile, data: @NotNull TranslationData)

    /**
     * Строит соответствующие данные для представления указанного файла перевода. (с учетом пространства имен и локали).
     * @param data Кэшированные данные перевода
     * @param file Целевой файл перевода
     * @return Строка, представляющая целевой файл перевода.
     * Может быть null, чтобы указать, что файл не требуется и может быть удален.
     */
    @Throws(Exception::class)
    abstract fun write(data: @NotNull TranslationData, file: @NotNull TranslationFile): @Nullable String?

    /**
     * Определяет узел перевода для использования при разборе
     * @param file Файл перевода для разбора
     * @param data Данные перевода
     * @return Узел перевода, который будет использоваться
     */
    protected fun getOrCreateTargetNode(file: @NotNull TranslationFile, data: @NotNull TranslationData): @NotNull TranslationNode {
        var targetNode = data.rootNode

        if (file.namespace != null) {
            targetNode = data.getOrCreateNode(file.namespace)
        }

        return targetNode
    }

    /**
     * Определяет узел перевода для записи
     * @param data Данные перевода
     * @param file Файл перевода для обновления
     * @return Узел перевода, который будет использоваться
     */
    protected fun getTargetNode(data: @NotNull TranslationData, file: @NotNull TranslationFile): @NotNull TranslationNode {
        var targetNode = data.rootNode

        if (file.namespace != null) {
            targetNode = data.getNode(KeyPath(file.namespace))!!
        }

        return targetNode ?: throw NullPointerException("Target node should not be null")
    }
}