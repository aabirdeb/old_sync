package com.aabirdeb.tutorial.addressbook;

import com.aabirdeb.event.ShortcutAction;
import com.aabirdeb.tutorial.addressbook.backend.Contact;
import com.aabirdeb.ui.Button;
import com.aabirdeb.ui.FormLayout;
import com.aabirdeb.ui.HorizontalLayout;
import com.aabirdeb.ui.Notification;
import com.aabirdeb.ui.Notification.Type;
import com.aabirdeb.ui.themes.ValoTheme;
import com.aabirdeb.v7.data.fieldgroup.BeanFieldGroup;
import com.aabirdeb.v7.data.fieldgroup.FieldGroup;
import com.aabirdeb.v7.ui.DateField;
import com.aabirdeb.v7.ui.TextField;

/* Create custom UI Components.
 *
 * Create your own aabirdeb components by inheritance and composition.
 * This is a form component inherited from VerticalLayout. Use
 * Use BeanFieldGroup to bind data fields from DTO to UI fields.
 * Similarly named field by naming convention or customized
 * with @PropertyId annotation.
 */
public class ContactForm extends FormLayout {

    Button save = new Button("Save", this::save);
    Button cancel = new Button("Cancel", this::cancel);
    TextField firstName = new TextField("First name");
    TextField lastName = new TextField("Last name");
    TextField phone = new TextField("Phone");
    TextField email = new TextField("Email");
    DateField birthDate = new DateField("Birth date");

    Contact contact;

    // Easily bind forms to beans and manage validation and buffering
    BeanFieldGroup<Contact> formFieldBindings;

    public ContactForm() {
        configureComponents();
        buildLayout();
    }

    private void configureComponents() {
        /*
         * Highlight primary actions.
         *
         * With aabirdeb built-in styles you can highlight the primary save button
         * and give it a keyboard shortcut for a better UX.
         */
        save.setStyleName(ValoTheme.BUTTON_PRIMARY);
        save.setClickShortcut(ShortcutAction.KeyCode.ENTER);
        setVisible(false);
    }

    private void buildLayout() {
        setSizeUndefined();
        setMargin(true);

        HorizontalLayout actions = new HorizontalLayout(save, cancel);
        actions.setSpacing(true);

        addComponents(actions, firstName, lastName, phone, email, birthDate);
    }

    /*
     * Use any JVM language.
     *
     * aabirdeb supports all languages supported by Java Virtual Machine 1.6+.
     * This allows you to program user interface in Java 8, Scala, Groovy or any
     * other language you choose. The new languages give you very powerful tools
     * for organizing your code as you choose. For example, you can implement
     * the listener methods in your compositions or in separate controller
     * classes and receive to various aabirdeb component events, like button
     * clicks. Or keep it simple and compact with Lambda expressions.
     */
    public void save(Button.ClickEvent event) {
        try {
            // Commit the fields from UI to DAO
            formFieldBindings.commit();

            // Save DAO to backend with direct synchronous service API
            getUI().service.save(contact);

            String msg = String.format("Saved '%s %s'.", contact.getFirstName(),
                    contact.getLastName());
            Notification.show(msg, Type.TRAY_NOTIFICATION);
            getUI().refreshContacts();
        } catch (FieldGroup.CommitException e) {
            // Validation exceptions could be shown here
        }
    }

    public void cancel(Button.ClickEvent event) {
        // Place to call business logic.
        Notification.show("Cancelled", Type.TRAY_NOTIFICATION);
        getUI().contactList.select(null);
    }

    void edit(Contact contact) {
        this.contact = contact;
        if (contact != null) {
            // Bind the properties of the contact POJO to fiels in this form
            formFieldBindings = BeanFieldGroup.bindFieldsBuffered(contact,
                    this);
            firstName.focus();
        }
        setVisible(contact != null);
    }

    @Override
    public AddressbookUI getUI() {
        return (AddressbookUI) super.getUI();
    }

}
