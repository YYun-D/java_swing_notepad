package project1;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

public class MyFrame extends JFrame{
	BufferedReader br = new BufferedReader(new FileReader("word.txt"));
	String[][][] SubjectWord = new String[6][][];
	JButton[] Subjects = new JButton[6];
	int cntSubject=1,count;
	int currentSubject;
	String tempSubjectName;
	String line;
	String[] columnNames = {"�ܾ�","��"};
	JTable[] wordTable = new JTable[6];
	JScrollPane[] scroll_table = new JScrollPane[6];
	int[] SubjectWordCnt= {0,0,0,0,0,0};
	MyFrame() throws IOException{
		ImageIcon icon2 = new ImageIcon("noteIcon.png");
		JButton startButton = new JButton("start");  	//ó�� start �� �ڷ� ���� ��ư
		JButton addSubjectButton = new JButton("+"); 	//���� �߰� ��ư
		JButton testButton = new JButton("test");    	//�ش� ���� �ִ� �͵� �׽�Ʈ ��ư
		JButton deleteCurSubject = new JButton("del");	//�ش� ���� ���� ��ư
		JButton addWord = new JButton("add");         	//�ش� ���� ���̺� ���ο� �ܾ� �߰� ��ư
		JTextField subjectText = new JTextField();      //���� �߰��� �� ���� �ؽ�Ʈ�ʵ� 
		//SubjectWord��� �� ���� �� �ܾ� ����� �迭 ����
		SubjectWord[0]=new String[500][2];
		for(int i=1;i<6;i++) 
			SubjectWord[i]=new String[100][2];
		//Subjects��� �� ���� �� ���� ȭ�鿡�� Ŭ���� ��ư ����
		for(int i=0;i<6;i++) {
			Subjects[i] = new JButton();
			Subjects[i].setVisible(false);
			Subjects[i].setFont(new Font("Comic Sans", Font.BOLD, 30));
			Subjects[i].setBounds(50+350*((i)%2),100+(i)/2*150,200,100);
			this.add(Subjects[i]);
		}
		//Subjects ������ �� �������� ��ũ�� ������ ���̺� ����
		for(int i=0;i<6;i++) {
			wordTable[i] = new JTable(SubjectWord[i],columnNames);
			wordTable[i].setEnabled(false);
			scroll_table[i] = new JScrollPane(wordTable[i]);
		    scroll_table[i].setVisible(false);
		    scroll_table[i].setBounds(100,100,400,400);
	        this.add(scroll_table[i]);
		}
		
		//���� ������ ������ �ִ� �� �ҷ��ͼ� �迭�� �־��ֱ�
		int wordcnt=0;
		int subjectcnt=1;
		line = br.readLine();
		count = Integer.parseInt(line);
		cntSubject=count;
		Subjects[0].setText("ALL");
		for(int i=1;i<count;i++) 
			Subjects[i].setText(br.readLine());
		while(true) {
		 	line = br.readLine();
            if (line==null) break;
            count = Integer.parseInt(line);
		 	for(int i=0;i<count;i++) {
	            line = br.readLine();
	            String[] wordline = line.split(" ");
		 		SubjectWord[0][wordcnt][0]=wordline[0];
	            SubjectWord[0][wordcnt++][1]=wordline[1];
	            SubjectWord[subjectcnt][i][0]=wordline[0];
	            SubjectWord[subjectcnt][i][1]=wordline[1];
		 	}
			SubjectWordCnt[subjectcnt]+=count;
			SubjectWordCnt[0]+=count;
		 	subjectcnt++;
        }
		
		
		subjectText.setVisible(false);
		subjectText.setFont(new Font("Comic Sans", Font.BOLD, 20));
		
		testButton.setFont(new Font("Comic Sans", Font.BOLD, 30));
		testButton.setVisible(false);
		testButton.setBounds(50,20,100,50);
		
		deleteCurSubject.setFont(new Font("Comic Sans", Font.BOLD, 30));
		deleteCurSubject.setVisible(false);
		deleteCurSubject.setBounds(170,20,100,50);

		addWord.setFont(new Font("Comic Sans", Font.BOLD, 30));
		addWord.setVisible(false);
		addWord.setBounds(290,20,100,50);

		addSubjectButton.setBounds(50+350*((cntSubject)%2),100+(cntSubject)/2*150,200,100);
		addSubjectButton.setFont(new Font("Comic Sans", Font.BOLD, 50));
		addSubjectButton.setVisible(false);
		
		startButton.setBounds(225,400,180,100);
		startButton.setFocusable(false);
		startButton.setVerticalTextPosition(JButton.BOTTOM);
		startButton.setFont(new Font("Comic Sans", Font.BOLD, 30));
		
		//���ο� ���� �߰��ϱ� ��ư
		addSubjectButton.addActionListener(e -> {
			addSubjectButton.setVisible(false);
			for(int i=0;i<cntSubject;i++) 
				Subjects[i].setEnabled(false);
			if(cntSubject<6) {
				Subjects[cntSubject].setVisible(false);
				subjectText.setBounds(50+350*((cntSubject)%2),100+(cntSubject)/2*150,200,100);
				subjectText.setVisible(true);
				cntSubject++;
				if(cntSubject==6) //5���� �� ���� �� �̻� �߰� ���ϰ� ����
					addSubjectButton.setVisible(false);
			}
		});
		//�� ������ Subjects[i]�� ������ �� 
		for(int i=0;i<6;i++) {
			int k=i;
			Subjects[i].addActionListener(e -> {
				currentSubject=k;
				startButton.setVisible(true);
				testButton.setVisible(true);
				deleteCurSubject.setVisible(true);
				addWord.setVisible(true);
				scroll_table[currentSubject].setVisible(true);
				for(int j=0;j<6;j++) 
					Subjects[j].setVisible(false);
				addSubjectButton.setVisible(false);
			});
		}
		//���� ���� ��ü �����ϱ�
		deleteCurSubject.addActionListener(e -> {
			int a=JOptionPane.showConfirmDialog(this,"Delete this Subject?"); 
			if(a==JOptionPane.YES_OPTION){  
				for(int i=currentSubject;i<5;i++) {
					Subjects[i].setText(Subjects[i+1].getText());
					for(int j=0;j<Math.max(SubjectWordCnt[i],SubjectWordCnt[i+1]);j++) {
						SubjectWord[i][j]=SubjectWord[i+1][j];
					}
				}
				int cnt=0;
				for(int i=1;i<6;i++) {
					for(int j=0;j<SubjectWordCnt[i];j++) {
						SubjectWord[0][cnt++]=SubjectWord[i][j];
					}
				}
				startButton.setBounds(410,20,180,50);
				startButton.setText("<-");
				startButton.setVisible(false);
				testButton.setVisible(false);
				deleteCurSubject.setVisible(false);
				addWord.setVisible(false);
				cntSubject-=1;
				for(int i=0;i<cntSubject;i++) {
					scroll_table[i].setVisible(false);
					Subjects[i].setVisible(true);
				}

				scroll_table[cntSubject].setVisible(false);
				addSubjectButton.setBounds(50+350*((cntSubject)%2),100+(cntSubject)/2*150,200,100);
				if(cntSubject<6)
					addSubjectButton.setVisible(true);
			}
		});
		//����ȭ������ ����
		startButton.addActionListener(e -> {
			startButton.setBounds(410,20,180,50);
			startButton.setText("<-");
			startButton.setVisible(false);
			testButton.setVisible(false);
			deleteCurSubject.setVisible(false);
			addWord.setVisible(false);
			for(int i=0;i<cntSubject;i++) {
				scroll_table[i].setVisible(false);
				Subjects[i].setVisible(true);
			}
			if(cntSubject<6)
				addSubjectButton.setVisible(true);
		});
		
		// �ܾ� �߰� ��ư �����
		addWord.addActionListener(e -> {
		  JTextField WordField = new JTextField(10);
	      JTextField MeanField = new JTextField(10);

	      JPanel myPanel = new JPanel();
	      myPanel.add(new JLabel("Word:"));
	      myPanel.add(WordField);
	      myPanel.add(Box.createHorizontalStrut(20));
	      myPanel.add(new JLabel("Mean:"));
	      myPanel.add(MeanField);
	      
	      int result = JOptionPane.showConfirmDialog(null, myPanel, "Please Enter Word and Mean", JOptionPane.OK_CANCEL_OPTION);
	      if (result == JOptionPane.OK_OPTION) {
	        String Word = WordField.getText();
			String Mean = MeanField.getText();
			SubjectWord[currentSubject][SubjectWordCnt[currentSubject]][0]=Word;
			SubjectWord[currentSubject][SubjectWordCnt[currentSubject]++][1]=Mean;
			SubjectWord[0][SubjectWordCnt[0]][0]=Word;
			SubjectWord[0][SubjectWordCnt[0]++][1]=Mean;
			scroll_table[currentSubject].setVisible(false);
			scroll_table[currentSubject].setVisible(true);
	      }
		});
		
        //TextField�� �߰� �� ������ �̸��� �Է¹޾��� ��, �� �̸��� ���� ���� ��ư �����
		subjectText.addActionListener(new ActionListener() {
		    @Override
		    public void actionPerformed(ActionEvent e) {
		    	if(subjectText.getText().equals("")) {
		    		cntSubject--;
		    	}
		    	else {
		    		tempSubjectName=subjectText.getText();
			    	Subjects[cntSubject-1].setVisible(true);
			    	Subjects[cntSubject-1].setText(tempSubjectName);
			    	subjectText.setText("");
					addSubjectButton.setBounds(50+350*((cntSubject)%2),100+(cntSubject)/2*150,200,100);
		    	}
		    	if(cntSubject!=6)
		    		addSubjectButton.setVisible(true);
				subjectText.setVisible(false);
				for(int i=0;i<cntSubject;i++) {
					Subjects[i].setEnabled(true);
				}
		    }
		});
		
		this.setIconImage(icon2.getImage());
        this.setTitle("Fancy Note Pad");
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(650,650);
        this.setLayout(null);
        this.setVisible(true);
        this.getContentPane().setBackground(new Color(0xf8b195));
        this.add(startButton);
        this.add(addSubjectButton);
        this.add(deleteCurSubject);
        this.add(subjectText);
        this.add(testButton);
        this.add(addWord);
        
        br.close();
        
        this.addWindowListener(new java.awt.event.WindowAdapter() {
	        @Override
			public void windowClosing(java.awt.event.WindowEvent windowEvent){
	        	try {
	        		saveFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
				System.exit(0);
			}
        });
	}
	void saveFile() throws IOException {
    	FileWriter fw = new FileWriter("word.txt");
		fw.write(cntSubject+"\n");
		for(int i=1;i<cntSubject;i++) {
		 	fw.write(Subjects[i].getText()+"\n");
		}
		for(int i=1;i<cntSubject;i++) {
			fw.write(SubjectWordCnt[i]+"\n");
			for(int j=0;j<SubjectWordCnt[i];j++) {
				fw.write(SubjectWord[i][j][0]+" "+SubjectWord[i][j][1]+"\n");
			}
		}
		fw.close();
    }
}
