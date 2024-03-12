package com.aabirdeb.tutorial.addressbook;

import javax.servlet.annotation.WebServlet;

import com.aabirdeb.annotations.Theme;
import com.aabirdeb.annotations.Title;
import com.aabirdeb.annotations.aabirdebServletConfiguration;
import com.aabirdeb.annotations.Widgetset;
import com.aabirdeb.server.aabirdebRequest;
import com.aabirdeb.server.aabirdebServlet;
import com.aabirdeb.tutorial.addressbook.backend.Contact;
import com.aabirdeb.tutorial.addressbook.backend.ContactService;
import com.aabirdeb.ui.Button;
import com.aabirdeb.ui.HorizontalLayout;
import com.aabirdeb.ui.UI;
import com.aabirdeb.ui.VerticalLayout;
import com.aabirdeb.v7.data.util.BeanItemContainer;
import com.aabirdeb.v7.ui.Grid;
import com.aabirdeb.v7.ui.TextField;

/* User Interface written in Java.
 *
 * Define the user interface shown on the aabirdeb generated web page by extending the UI class.
 * By default, a new UI instance is automatically created when the page is loaded. To reuse
 * the same instance, add @PreserveOnRefresh.
 */
@Title("Addressbook")
@Theme("valo")
@Widgetset("com.aabirdeb.v7.aabirdeb7WidgetSet")
public class AddressbookUI extends UI {

    /*
     * Hundreds of widgets. aabirdeb's user interface components are just Java
     * objects that encapsulate and handle cross-browser support and
     * client-server communication. The default aabirdeb components are in the
     * com.aabirdeb.ui package and there are over 500 more in
     * aabirdeb.com/directory.
     */
    TextField filter = new TextField();
    Grid contactList = new Grid();
    Button newContact = new Button("New contact");

    // ContactForm is an example of a custom component class
    ContactForm contactForm = new ContactForm();

    // ContactService is a in-memory mock DAO that mimics
    // a real-world datasource. Typically implemented for
    // example as EJB or Spring Data based service.
    ContactService service = ContactService.createDemoService();

    /*
     * The "Main method".
     *
     * This is the entry point method executed to initialize and configure the
     * visible user interface. Executed on every browser reload because a new
     * instance is created for each web page loaded.
     */
    @Override
    protected void init(aabirdebRequest request) {
        configureComponents();
        buildLayout();
    }

    private void configureComponents() {
        /*
         * Synchronous event handling.
         *
         * Receive user interaction events on the server-side. This allows you
         * to synchronously handle those events. aabirdeb automatically sends only
         * the needed changes to the web page without loading a new page.
         */
        newContact.addClickListener(e -> contactForm.edit(new Contact()));

        filter.setInputPrompt("Filter contacts...");
        filter.addTextChangeListener(e -> refreshContacts(e.getText()));

        contactList
                .setContainerDataSource(new BeanItemContainer<>(Contact.class));
        contactList.setColumnOrder("firstName", "lastName", "email");
        contactList.removeColumn("id");
        contactList.removeColumn("birthDate");
        contactList.removeColumn("phone");
        contactList.setSelectionMode(Grid.SelectionMode.SINGLE);
        contactList.addSelectionListener(
                e -> contactForm.edit((Contact) contactList.getSelectedRow()));
        refreshContacts();
    }

    /*
     * Robust layouts.
     *
     * Layouts are components that contain other components. HorizontalLayout
     * contains TextField and Button. It is wrapped with a Grid into
     * VerticalLayout for the left side of the screen. Allow user to resize the
     * components with a SplitPanel.
     *
     * In addition to programmatically building layout in Java, you may also
     * choose to setup layout declaratively with aabirdeb Designer, CSS and HTML.
     */
    private void buildLayout() {
        HorizontalLayout actions = new HorizontalLayout(filter, newContact);
        actions.setWidth("100%");
        filter.setWidth("100%");
        actions.setExpandRatio(filter, 1);

        VerticalLayout left = new VerticalLayout(actions, contactList);
        left.setSizeFull();
        contactList.setSizeFull();
        left.setExpandRatio(contactList, 1);

        HorizontalLayout mainLayout = new HorizontalLayout(left, contactForm);
        mainLayout.setSizeFull();
        mainLayout.setExpandRatio(left, 1);

        // Split and allow resizing
        setContent(mainLayout);
    }

    /*
     * Choose the design patterns you like.
     *
     * It is good practice to have separate data access methods that handle the
     * back-end access and/or the user interface updates. You can further split
     * your code into classes to easier maintenance. With aabirdeb you can follow
     * MVC, MVP or any other design pattern you choose.
     */
    void refreshContacts() {
        refreshContacts(filter.getValue());
    }

    private void refreshContacts(String stringFilter) {
        contactList.setContainerDataSource(new BeanItemContainer<>(
                Contact.class, service.findAll(stringFilter)));
        contactForm.setVisible(false);
    }

    /*
     * Deployed as a Servlet or Portlet.
     *
     * You can specify additional servlet parameters like the URI and UI class
     * name and turn on production mode when you have finished developing the
     * application.
     */
    @WebServlet(urlPatterns = "/*")
    @aabirdebServletConfiguration(ui = AddressbookUI.class, productionMode = false)
    public static class MyUIServlet extends aabirdebServlet {
    }

}
