package sample;

import com.jfoenix.controls.JFXAutoCompletePopup;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

public class AutoCompleteComboBox<T> {
    public AutoCompleteComboBox(final ComboBox<T> comboBox) {
        JFXAutoCompletePopup<T> autoCompletePopup = new JFXAutoCompletePopup<>();
        autoCompletePopup.setSelectionHandler(e -> {
            comboBox.setValue(e.getObject());
        });
        TextField editor = comboBox.getEditor();
        editor.textProperty().addListener((o, oldVal, newVal) -> {
            autoCompletePopup.filter(item -> item.toString().toLowerCase().contains(editor.getText().toLowerCase()));
            if (autoCompletePopup.getFilteredSuggestions().isEmpty()
                    || comboBox.showingProperty().get())
                autoCompletePopup.hide();
            else
                autoCompletePopup.show(editor);
        });
        comboBox.getSelectionModel().selectedItemProperty().addListener((o, oldVal, newVal) -> {
            autoCompletePopup.hide();
        });
    }
}
