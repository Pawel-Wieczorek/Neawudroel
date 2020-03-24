package com.neawudroel;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import com.neawudroel.person;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;

public class Controller {

    //General stuff
    private static final EntityManagerFactory ENTITY_MANAGER_FACTORY = Persistence.createEntityManagerFactory("HibernateConcert");
    private EntityManager em = ENTITY_MANAGER_FACTORY.createEntityManager();

    //Concert tab
    public TextField txtArtist;
    public DatePicker dateDate;
    public ListView<String> lvConcerts;
    public ListView<String> lvAttendees;
    private List<concert> concertList = em.createQuery("SELECT c FROM concert AS c").getResultList();
    private ObservableList<String> concertObsList = FXCollections.observableArrayList();
    private List<List<Integer>> concertpersonList = new ArrayList<>();

    //People tab
    public TextField txtName;
    public DatePicker dateAge;
    public ListView<String> lvPeople;
    public ListView<String> lvAtConcerts;
    private List<person> personList = em.createQuery("SELECT a FROM person AS a").getResultList();
    private ObservableList<String> peopleObsList = FXCollections.observableArrayList();
    private List<List<Integer>> personconcertList = new ArrayList<>();

    //Setup event listener and set the selectionMode to be multiple. Then pass the listView to a handler that makes them act like slectionBoxes
    public void initialize(){
        lvAtConcerts.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        lvAttendees.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        listViewHandler(lvAtConcerts);
        listViewHandler(lvAttendees);

        attendeeconcertLoad();

        lvPeople.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            highlighter(personconcertList, lvAtConcerts, lvPeople.getSelectionModel().getSelectedIndex());
        });
        lvConcerts.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("Highlight Sent: " +lvConcerts.getSelectionModel().getSelectedIndex());
            highlighter(concertpersonList, lvAttendees, lvConcerts.getSelectionModel().getSelectedIndex());

        });

        updateListings();
    }

    //Adds an eventHandler to a given listview so each item will act more like a checkbox / toggle button
    //Graciously provided by fabian from stackoverflow @ https://stackoverflow.com/a/40906993
    private void listViewHandler(ListView<String> listview) {
        listview.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
            Node node = event.getPickResult().getIntersectedNode();

            //Go up from the target node until a list cell is found or it's clear it was not a cell that was clicked
            while(node != null && node != lvAttendees && !(node instanceof ListCell)){
                node = node.getParent();
            }

            //If node is part of a cell or is the cell handle the event instead of using standard handling
            if(node instanceof ListCell){
                event.consume();

                ListCell<String> cell = (ListCell) node;
                ListView<String> lv = cell.getListView();

                //Focus the listview
                lv.requestFocus();

                if(!cell.isEmpty()){
                    //Handle selection of non-empty cells
                    int index = cell.getIndex();
                    if(cell.isSelected()){
                        lv.getSelectionModel().clearSelection(index);
                    }else{
                        lv.getSelectionModel().select(index);
                    }
                }
            }
        });
    }

    //Passes the relationList, listView and what index(person or concert) to highlight.
    private void highlighter(List<List<Integer>> relationList, ListView<String> chosenListView, int index){
        try {
            System.out.println("Highlighting all from: " + index + " personconcertList Size: " + relationList.get(index).size());
            chosenListView.getSelectionModel().clearSelection();
            for (int i = 0; i < relationList.get(index).size(); i++) {
                int concertID = relationList.get(index).get(i) - 1;
                chosenListView.getSelectionModel().select(concertID);
                System.out.println("Selecting list item: " + concertID);
            }
        }catch(IndexOutOfBoundsException e){
            System.out.println("No items");
            chosenListView.getSelectionModel().clearSelection();
        }
    }

    //creates two local relationLists based off of attendeeconcert to handle proper highlighting.
    private void attendeeconcertLoad(){
        List<attendeeconcert> tempPersonConcertList = new ArrayList<>();
        List<attendeeconcert> tempConcertPersonList = new ArrayList<>();

        //This entire SQL thing is used because the other commented Hibernate JPA thing doesn't work, the list it gets is wrong and I cannot figure out why. This SQL works though.
        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/hibernateconcert", "javauser", "java");
            Statement st = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            String sql = "SELECT * FROM attendeeconcert ORDER BY personID";
            ResultSet result = st.executeQuery(sql);
            while (result.next()) {
                tempPersonConcertList.add(new attendeeconcert(result.getInt("personID"), result.getInt("concertID")));
            }

            sql = "SELECT * FROM attendeeconcert ORDER BY concertID";
            result = st.executeQuery(sql);

            while (result.next()) {
                tempConcertPersonList.add(new attendeeconcert(result.getInt("personID"), result.getInt("concertID")));
            }

        }catch (SQLException e){
            e.printStackTrace();
        }

        //tempPersonConcertList = em.createQuery("SELECT a FROM attendeeconcert AS a ORDER BY a.personID").getResultList();
        personconcertList.clear();
        concertpersonList.clear();
        System.out.println("List gotten: ");
        for (attendeeconcert attendeeconcert : tempPersonConcertList) {
            System.out.println("    PersonID: " + attendeeconcert.getPersonID() + " ConcertID: " + attendeeconcert.getConcertID());
        }
        System.out.println("---------------");
        for ( attendeeconcert attendeeconcert : tempConcertPersonList) {
            System.out.println("    ConcertID: " + attendeeconcert.getConcertID() + " PersonID: " + attendeeconcert.getPersonID());
        }

        for(int i = 0; i < tempPersonConcertList.size(); i++){
            if(tempPersonConcertList.get(i).getPersonID() != personconcertList.size()){
                personconcertList.add(new ArrayList<>());
            }
            personconcertList.get(personconcertList.size()-1).add(tempPersonConcertList.get(i).getConcertID());
        }

        for(int i = 0; i < tempConcertPersonList.size(); i++){
            if(tempConcertPersonList.get(i).getConcertID() != concertpersonList.size()) {
                concertpersonList.add(new ArrayList<>());
            }
            concertpersonList.get(concertpersonList.size()-1).add(tempConcertPersonList.get(i).getPersonID());

        }
    }

    //Creates an EntityManager which then creates a temporary new attendeeconcert object (with the appropriate parameters) and then passes it to the DB
    private void changeAttendee(int personID, int concertID, boolean remove){
        System.out.println("Got: " +personID +", " +concertID +" remove: " +remove);
        EntityManager em = ENTITY_MANAGER_FACTORY.createEntityManager();
        EntityTransaction et = null;

        try{
            et = em.getTransaction();
            et.begin();
            attendeeconcert ac = new attendeeconcert(personID, concertID);
                if(remove){
                    //TODO
                    // I can't for the life of me get this to work, so for now removing a listing isn't possible (unless done through the DB).
                    // em.remove(ac); <--this needs to be in the same transaction, so I'm a bit unsure on how to get it all working.
                    //System.out.println("Removed: " +ac.toString());
                }else{

                    em.persist(ac);
                    System.out.println("Added: " +ac.toString());
                }

                et.commit();
        }catch (Exception ex){
            if(et != null){
                et.rollback();
            }
            ex.printStackTrace();
        }finally {
            em.close();
            attendeeconcertLoad();
        }
    }

    //Starts a transaction to pass a newly created concert Object to the database
    public void btnAddConcert() {
        EntityManager em = ENTITY_MANAGER_FACTORY.createEntityManager();
        EntityTransaction et = null;
        try{
            et = em.getTransaction();
            et.begin();
            Date date = Date.valueOf(dateDate.getValue());
            concertList.add(new concert(concertList.size()+1, date, txtArtist.getText()));
            System.out.println("New Concert added: " +concertList.get(concertList.size()-1).toString());
            em.persist(concertList.get(concertList.size()-1));
            et.commit();
        }catch (Exception ex){
            if(et != null){
                et.rollback();
            }

            ex.printStackTrace();
        }finally {
            em.close();
            updateListings();
        }
    }

    //Same as above but with Person
    public void btnAddPerson(){
        EntityManager em = ENTITY_MANAGER_FACTORY.createEntityManager();
        EntityTransaction et = null;
        try{
            et = em.getTransaction();
            et.begin();
            Date age = Date.valueOf(dateAge.getValue());
            personList.add(new person(personList.size()+1, age, txtName.getText()));
            System.out.println("New person added: " +personList.get(personList.size()-1).toString());
            em.persist(personList.get(personList.size()-1));
            et.commit();
        }catch (Exception ex){
            if(et != null){
                et.rollback();
            }

            ex.printStackTrace();
        }finally {
            em.close();
            updateListings();
        }
    }

    //Clears the observable lists then repopulates them with the items in the object Lists
    //Finally sets the listView to the observableLists
    private void updateListings(){
        concertObsList.clear();
        for ( concert concert : concertList) {
            concertObsList.add(concert.getArtist());
            System.out.println("Listing updated: " +concert.toString());
        }

        peopleObsList.clear();
        for ( person person : personList) {
            peopleObsList.add(person.getName());
            System.out.println("Listing updated: " +person.toString());
        }


        lvAtConcerts.setItems(concertObsList);
        lvConcerts.setItems(concertObsList);
        lvAttendees.setItems(peopleObsList);
        lvPeople.setItems(peopleObsList);
    }

    //These two functions adds (supposed to change) the attendee or concerts
    public void lvAttendeeClick() {
        int selectedPerson = lvAttendees.getSelectionModel().getSelectedIndex()+1;
        int selectedConcert = lvConcerts.getSelectionModel().getSelectedIndex()+1;
        boolean selected = !lvAttendees.getSelectionModel().isSelected(selectedPerson-1);
        changeAttendee(selectedPerson , selectedConcert, selected);
    }

    public void lvAtConcertsClick() {
        int selectedPerson = lvPeople.getSelectionModel().getSelectedIndex()+1;
        int selectedConcert = lvAtConcerts.getSelectionModel().getSelectedIndex()+1;
        boolean selected = !lvAtConcerts.getSelectionModel().isSelected(selectedConcert-1);
        changeAttendee(selectedPerson, selectedConcert, selected);
    }
}
