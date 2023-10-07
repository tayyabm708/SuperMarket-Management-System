package Employee;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.bson.conversions.Bson;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class ApproveOrderMenu extends JFrame implements ActionListener {
    MongoClientURI uri = new MongoClientURI("mongodb://localhost:27017");
    MongoClient mongoClient = new MongoClient(uri);
    //Retrieve your DB
    MongoDatabase database = mongoClient.getDatabase("EzShpop");
    //Accessing Employee's Collection
    MongoCollection<Document> collection = database.getCollection("Orders");

    JTable Product;
    JButton det,cancel;
    String username;
    ApproveOrderMenu(String str) {
        username=str;
        setTitle("Orders");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setResizable(false);
        setSize(700, 600);
        setLayout(null);
        setLocationRelativeTo(null);
        ImageIcon mainlogo = new ImageIcon("images\\mainlogo.png");
        setIconImage(mainlogo.getImage());

        JPanel head = new JPanel();
        head.setBounds(0, 0, 700, 100);
        head.setBackground(new Color(44, 62, 80));
        head.setLayout(null);
        JLabel heading = new JLabel("Placed Orders");
        heading.setFont(new Font("Futura", Font.BOLD, 35));
        heading.setBounds(30, 25, 400, 50);
        heading.setForeground(Color.white);
        head.add(heading);

        String[] col = {"No.", "Order ID", "Items", "Total (Rs.)", "Shipping Status"};

        DefaultTableModel Tb = new DefaultTableModel(col, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        Product = new JTable(Tb);
        try {
            MongoCursor<Document> cursor = collection.find().iterator();


            int no = 1;
            while(cursor.hasNext()) {
                Document doc = cursor.next();
                boolean status = doc.getBoolean("STATUS");
                if(!status){
                    int orderId = doc.getInteger("ORDER_ID");
                    String productName = doc.getString("ITEMS");
                    double total = doc.getDouble("TOTAL");
                    Object[] obj = {no, orderId, productName ,total , "Pending"};
                    Tb.addRow(obj);
                    no++;
                }
            }
        }
        catch (Exception throwables) {
            throwables.printStackTrace();
        }
        Product.setFont(new Font("Futura", Font.PLAIN, 15));
        Product.setRowHeight(30);
        TableColumnModel columnModel = Product.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(40);
        columnModel.getColumn(1).setPreferredWidth(80);
        columnModel.getColumn(2).setPreferredWidth(400);
        columnModel.getColumn(3).setPreferredWidth(80);
        columnModel.getColumn(4).setPreferredWidth(100);


        JScrollPane body = new JScrollPane(Product);
        body.setBounds(1, 100, 687, 400);
        body.setBackground(new Color(234, 250, 241));


        JPanel footer = new JPanel();
        footer.setBounds(0, 500, 700, 400);
        footer.setBackground(new Color(93, 109, 126));
        footer.setLayout(null);

        JButton back = new JButton("Back");
        back.setForeground(Color.white);
        back.setBackground(new Color(203, 67, 53));
        back.setBounds(600, 20, 70, 30);
        back.setFocusable(false);
        back.setFont(new Font("Tahoma", Font.PLAIN, 12));
        back.addActionListener(E -> {
            new EmployeeMenu(username);
            dispose();
        });
        footer.add(back);

        det = new JButton("Details");
        det.setForeground(Color.white);
        det.setBackground(new Color(230, 126, 34));
        det.setBounds(30, 20, 70, 30);
        det.setFocusable(false);
        det.setFont(new Font("Didot", Font.PLAIN, 12));
        det.addActionListener(this);
        footer.add(det);

        cancel = new JButton("Approve Order");
        cancel.setForeground(Color.white);
        cancel.setBackground(new Color(22, 160, 133));
        cancel.setBounds(270, 10, 150, 40);
        cancel.setFocusable(false);
        cancel.setFont(new Font("Didot", Font.BOLD, 16));
        cancel.addActionListener(this);
        footer.add(cancel);


        setVisible(true);
        add(head);
        add(body);
        add(footer);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource()==det){
            if(Product.getSelectedRow()!=-1){
                JOptionPane.showMessageDialog(null,Product.getModel().getValueAt(Product.getSelectedRow(),2),"ITEMS",JOptionPane.INFORMATION_MESSAGE);
            }
            else
                JOptionPane.showMessageDialog(null,"Select an Order to Show Details","Dumb 101",JOptionPane.ERROR_MESSAGE);
        }
        if(e.getSource()==cancel){
            if(Product.getSelectedRow()!=-1){
                int opt=JOptionPane.showConfirmDialog(null,"Ship Order?","Sure?",JOptionPane.OK_CANCEL_OPTION,JOptionPane.QUESTION_MESSAGE);
                if(opt==0) {
                    try {
                        int orderId = Integer.parseInt(String.valueOf(Product.getModel().getValueAt(Product.getSelectedRow(), 1)));

                        Bson filter = Filters.eq("ORDER_ID", orderId);
                        Bson update = Updates.set("STATUS", true);

                        UpdateResult result = collection.updateOne(filter, update);

                        if (result.getModifiedCount() > 0) {
                            JOptionPane.showMessageDialog(null, "Order Approved", "Shipped", JOptionPane.INFORMATION_MESSAGE);
                            new ApproveOrderMenu(username);
                            dispose();
                        } else {
                            JOptionPane.showMessageDialog(null, "Failed to update order", "Error", JOptionPane.ERROR_MESSAGE);
                        }

                    } catch (Exception throwables) {
                        throwables.printStackTrace();
                    }
                }
            }
            else
                JOptionPane.showMessageDialog(null,"Select an Order","SMH",JOptionPane.WARNING_MESSAGE);
        }

    }
}
