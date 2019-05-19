package ATJava;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.*;


import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.event.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;


public class MainForm extends JFrame {
    private JButton checkAtt;
    private JPanel contentPane;
    private JPanel centralPane;
    private JPanel footerPane;
    private JPanel dataPane;
    private JPanel changeReportPane;
    private JLabel dbStatus;
    private JTable studentAttendanceTable;
    private JComboBox disciplineComboBox;
    private JTextField groupTextField;
    private JButton makeReportButton;
    private JList attendingStudentsList;
    private JTable classesTable;

    private PswDialogResponse AuthData;
    private PswDialogResponse AuthDB;
    private LoginForm dialog = new LoginForm(this);

    public MainForm() {
        super("Main form");

        setContentPane(contentPane);
        pack();
        setVisible(true);

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onPress();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onPress();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        dataAcessInitialization();
        final DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("attendancetracking-android");
        loginFormDatainitializing(ref);
        while (makeAuth(ref)) ;
        userNameUpdate(ref);

        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.getKey().equals("Disciplines"))
                    fillDisciplines(dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.getKey().equals("Disciplines"))
                    fillDisciplines(dataSnapshot);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getKey().equals("Disciplines"))
                    fillDisciplines(dataSnapshot);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.getKey().equals("Disciplines"))
                    fillDisciplines(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });

        disciplineComboBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                int state = e.getStateChange();
                if (state == ItemEvent.SELECTED) {
                    ref.addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            fillClassesTable(dataSnapshot);
                        }

                        @Override
                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                            fillClassesTable(dataSnapshot);
                        }

                        @Override
                        public void onChildRemoved(DataSnapshot dataSnapshot) {
                            fillClassesTable(dataSnapshot);
                        }

                        @Override
                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                            fillClassesTable(dataSnapshot);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                }
            }
        });

        studentAttendanceTable.setDefaultEditor(Object.class, null);
        attendingStudentsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        classesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        classesTable.setDefaultEditor(Object.class, null);
        classesTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(final ListSelectionEvent e) {
                ref.addChildEventListener(new ChildEventListener() {
                    private void fillIfCorrectSnapshot(DataSnapshot dataSnapshot) {
                        if (e.getValueIsAdjusting())
                            return;
                        for (DataSnapshot classSnapshot : dataSnapshot.getChildren()) {
                            TableModel model = classesTable.getModel();
                            int index = classesTable.getSelectedRow();

                            if (classSnapshot.child("discipline").getValue().toString().equals(disciplineComboBox.getSelectedItem().toString()) &&
                                    classSnapshot.getKey().equals(model.getValueAt(index, 0))) {
                                for (DataSnapshot groupSnapshot : classSnapshot.child("groups").getChildren()) {
                                    if (groupSnapshot.getKey().equals(model.getValueAt(index, 1).toString()))
                                        fillAttendanceList(groupSnapshot);
                                }
                            }
                        }
                    }

                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        fillIfCorrectSnapshot(dataSnapshot);
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                        fillIfCorrectSnapshot(dataSnapshot);
                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {
                        fillIfCorrectSnapshot(dataSnapshot);
                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                        fillIfCorrectSnapshot(dataSnapshot);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });

        attendingStudentsList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                ref.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        fillStudentReport(dataSnapshot);
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                        fillStudentReport(dataSnapshot);
                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {
                        fillStudentReport(dataSnapshot);
                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                        fillStudentReport(dataSnapshot);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });

        getRootPane().setDefaultButton(checkAtt);
        checkAtt.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addClass(ref.child("Classes"));
            }
        });
    }

    private void addClass(final DatabaseReference classesRef) {
        Vector<String> disciplineList = new Vector<>();
        for (int i = 0; i < disciplineComboBox.getModel().getSize(); ++i) {
            disciplineList.add(disciplineComboBox.getItemAt(i).toString());
        }
        ClassCreator creator = new ClassCreator(disciplineList, this);
        creator.pack();
        creator.setVisible(true);

        ClassCreator.Data data = creator.data;
        if (!data.discipline.isEmpty() && !data.timestamp.isEmpty()) {
            HashMap<String, Object> newClass = new HashMap<>();
            newClass.put("discipline", data.discipline);
            newClass.put("tracking", "true");
            classesRef.child(data.timestamp).setValueAsync(newClass);
            TimerDialog dialog = new TimerDialog(this);
            dialog.pack();
            dialog.setLocation(200,100);
            dialog.setVisible(true);
            classesRef.child(data.timestamp).child("tracking").setValueAsync("false");

        }
    }

    private void fillAttendanceList(DataSnapshot dataSnapshot) {
        DefaultListModel model = new DefaultListModel();
        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
            model.addElement(snapshot.getValue().toString());
        }
        attendingStudentsList.setModel(model);
        if(attendingStudentsList.getVisibleRowCount() > 0)
            attendingStudentsList.setSelectedIndex(0);
    }

    private void fillClassesTable(DataSnapshot dataSnapshot) {
        DefaultTableModel dtm = new DefaultTableModel();
        dtm.addColumn("Date");
        dtm.addColumn("Group");
        for (DataSnapshot classSnapshot : dataSnapshot.getChildren()) {
            if (classSnapshot.child("discipline").getValue().toString().equals(disciplineComboBox.getSelectedItem().toString())) {
                for (DataSnapshot groupSnapshot : classSnapshot.child("groups").getChildren()) {
                    Vector<String> row = new Vector<>();
                    row.add(classSnapshot.getKey());
                    row.add(groupSnapshot.getKey());
                    dtm.addRow(row);
                }
            }
        }
        classesTable.setModel(dtm);
        if(classesTable.getRowCount() > 0)
            classesTable.setRowSelectionInterval(0, 0);
    }

    private void fillStudentReport(DataSnapshot snapshot) {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Date");
        model.addColumn("Attended");
        String group = classesTable.getModel().getValueAt(classesTable.getSelectedRow(), 1).toString();
        String studentName = attendingStudentsList.getModel().getElementAt(attendingStudentsList.getSelectedIndex()).toString();
        for (DataSnapshot classSnapshot : snapshot.getChildren()) {
            if (classSnapshot.child("discipline").getValue().toString().equals(disciplineComboBox.getSelectedItem().toString())) {
                for (DataSnapshot groupSnapshot : classSnapshot.child("groups").getChildren()) {
                    if (groupSnapshot.getKey().equals(group)) {
                        Vector<String> row = new Vector<>();
                        String timestamp = classSnapshot.getKey();
                        row.add(timestamp);
                        boolean studentAttended = false;
                        for (DataSnapshot studentSnapshot : groupSnapshot.getChildren()) {
                            if(studentSnapshot.getValue().toString().equals(studentName)) {
                                studentAttended = true;
                                break;
                            }
                        }
                        if (studentAttended) {
                            row.add("+");
                        } else row.add("-");
                        model.addRow(row);
                    }
                }
            }
        }
        studentAttendanceTable.setModel(model);
    }

    private void onPress() {
        System.exit(0);
    }

    private void fillDisciplines(DataSnapshot snapshot) {
        disciplineComboBox.removeAllItems();
        for (DataSnapshot disciplineSnapshot : snapshot.getChildren()) {
            disciplineComboBox.addItem(disciplineSnapshot.getValue().toString());
        }
    }

    public boolean makeAuth(DatabaseReference ref) {
        boolean status = true;

        setFocusable(false);
        setEnabled(false);


        dialog.pack();
        dialog.setLocation(200,100);
        dialog.setVisible(true);
        AuthData = new PswDialogResponse(dialog.GetResponse());
        if (AuthDB.getPassword().equals(AuthData.getPassword())) status = false;
        setFocusable(true);
        setEnabled(true);
        return status;
    }

    public DatabaseReference dataAcessInitialization() {
        try {
            FileInputStream serviceAccount =
                    new FileInputStream("attendancetracking_android_firebase.json");
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setDatabaseUrl("https://attendancetracking-android.firebaseio.com")
                    .build();
            FirebaseApp.initializeApp(options);
        } catch (FileNotFoundException fnfExc) {
        } catch (IOException ioExc) {
        }
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("attendancetracking-android");
        return ref;
    }

    private void loginFormDatainitializing(DatabaseReference rfr) {
        rfr.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                AuthDB = new PswDialogResponse(dataSnapshot.child("Username").getValue().toString(), dataSnapshot.child("Password").getValue().toString());
                dialog.loginSet(AuthDB.getEmail());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void userNameUpdate(DatabaseReference rfr) {
        if (!AuthDB.getEmail().equals(AuthData.getEmail())) {
            Map<String, Object> nameUpdates = new HashMap<>();
            nameUpdates.put("Username", AuthData.getEmail());
            rfr.updateChildrenAsync(nameUpdates);
        }
    }


}
