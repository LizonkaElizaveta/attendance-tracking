package ATJava;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;


import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;


public class MainForm extends JFrame {
    private JTable allTable;
    private JTabbedPane reportTable;
    private JTable presentTable;
    private JTable absentTable;
    private JButton checkAtt;
    private JPanel contentPane;
    private JPanel centralPane;
    private JPanel footerPane;
    private JPanel dataPane;
    private JPanel changeReportPane;
    private JLabel dbStatus;

    private PswDialogResponse AuthData;
    private PswDialogResponse AuthDB;
    private LoginForm dialog = new LoginForm();

    public MainForm() {
        super("Main form");

        setContentPane(contentPane);
        pack();
        setVisible(true);

        getRootPane().setDefaultButton(checkAtt);
        checkAtt.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onPress();
            }
        });

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
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("attendancetracking-android");
        loginFormDatainitializing(ref);
        while (makeAuth(ref));
        userNameUpdate(ref);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DefaultTableModel dtm = new DefaultTableModel();
                dtm.addColumn("UID");
                dtm.addColumn("Attendance");
                for (DataSnapshot userSnapshot: dataSnapshot.getChildren())
                {
                    for (DataSnapshot attSnapshot: userSnapshot.child("Attendance").getChildren()
                         ) {
                        Vector<String> rowVect = new Vector<String>();
                        rowVect.add(userSnapshot.getKey());
                        rowVect.add(attSnapshot.getValue().toString());
                        dtm.addRow(rowVect);
                        System.out.print(dtm);
                    }
                }
                allTable.setModel(dtm);
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });
    }

    private void onPress() {
        System.exit(0);
    }

    public boolean makeAuth(DatabaseReference ref) {
        boolean status = true;

        setFocusable(false);
        setEnabled(false);


        dialog.pack();
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
        }catch (FileNotFoundException fnfExc) {}
        catch (IOException ioExc) {}
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

    private void userNameUpdate (DatabaseReference rfr) {
        if (!AuthDB.getEmail().equals(AuthData.getEmail())) {
            Map<String, Object> nameUpdates = new HashMap<>();
            nameUpdates.put("Username", AuthData.getEmail());
            rfr.updateChildrenAsync(nameUpdates);
        }
    }


}
