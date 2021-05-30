package projectTTS;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

import javax.swing.*;

import com.google.cloud.texttospeech.v1.*;
import com.google.protobuf.ByteString;

public class MyFrame extends JFrame{
	BufferedReader br = new BufferedReader(new FileReader("word.txt"));
	static Object[][][] SubjectWord = new Object[6][][];
	JButton[] Subjects = new JButton[6];
	JButton[] deleteSubjects = new JButton[6];
	int cntSubject=1,count;
	static int currentSubject;
	String tempSubjectName;
	String line;
	Object[] columnNames = {"중요도","단어","뜻","선택"};
	JTable[] wordTable = new JTable[6];
	static int[] wordColor= {0xFFFF66,0xCCFF00,0x50BFE6,0xFD5B78,0xFF6EFF,0xAF6E4D};
	JScrollPane[] scroll_table = new JScrollPane[6];
	
	static int[] SubjectWordCnt= {0,0,0,0,0,0};
	
	VoiceSelectionParams voice =VoiceSelectionParams.newBuilder().setLanguageCode("en-US")
			.setSsmlGender(SsmlVoiceGender.NEUTRAL).build();
	AudioConfig audioConfig =AudioConfig.newBuilder().setAudioEncoding(AudioEncoding.MP3).build();
	MyFrame() throws IOException{
		ImageIcon icon2 = new ImageIcon("noteIcon.png");
		ImageIcon iconX = new ImageIcon("x.png");
		ImageIcon iconNote = new ImageIcon("img.png");
		ImageIcon iconStar = new ImageIcon("star.jpg");
		JButton startButton = new JButton("start");  	//처음 start 및 뒤로 가는 버튼
		JButton addSubjectButton = new JButton("+"); 	//주제 추가 버튼
		JButton testButton = new JButton("test");    	//해당 과목에 있는 것들 테스트 버튼
		JButton deleteSelectedWords = new JButton("del");	//해당 주제 삭제 버튼
		JButton addWord = new JButton("add");         	//해당 주제 테이블에 새로운 단어 추가 버튼
		JCheckBox selectAll = new JCheckBox(); 
		JTextField subjectText = new JTextField();      //주제 추가할 때 쓰는 텍스트필드
		JPanel selectBox= new JPanel();
		JLabel selectYN=new JLabel();
		//SubjectWord라는 각 주제 별 단어 저장용 배열 생성
		for(int i=0;i<6;i++) 
			SubjectWord[i]=new Object[100][4];
		//Subjects라는 각 주제 별 메인 화면에서 클릭할 버튼 생성
		for(int i=0;i<6;i++) {
			Subjects[i] = new JButton();
			Subjects[i].setVisible(false);
			Subjects[i].setBackground(new Color(wordColor[i]));
			Subjects[i].setFont(new Font("Comic Sans", Font.BOLD, 30));
			Subjects[i].setBounds(50+330*((i)%2),100+(i)/2*150,200,100);
			this.add(Subjects[i]);
			deleteSubjects[i]=new JButton();
			deleteSubjects[i].setVisible(false);
			deleteSubjects[i].setBackground(new Color(wordColor[i]));
			deleteSubjects[i].setIcon(iconX);
			deleteSubjects[i].setBounds(250+330*((i)%2),100+(i)/2*150,30,30);
			this.add(deleteSubjects[i]);
		}
		//Subjects 눌렀을 때 보여지는 스크롤 가능한 테이블 생성
		for(int i=0;i<6;i++) {
			wordTable[i] = new JTable(SubjectWord[i],columnNames) {
				@Override
				public Class getColumnClass(int column) {
				switch (column) {
					case 0:
						return Icon.class;
					case 1:
						return String.class;
					case 2:
						return String.class;
					default:
						return Boolean.class;
				}
		    	}
				@Override
				public boolean isCellEditable(int row, int col) {
					if(SubjectWordCnt[currentSubject]>row) {
						return (col==3);
					}
			        return col==4;
			    }
			};
			int k=i;
			wordTable[i].addMouseListener(new java.awt.event.MouseAdapter() {
			    @Override
			    public void mouseClicked(java.awt.event.MouseEvent evt) {
			        int row = wordTable[k].rowAtPoint(evt.getPoint());
			        int col = wordTable[k].columnAtPoint(evt.getPoint());
			        if (row<SubjectWordCnt[k]) {
				        if (col == 0 && SubjectWord[k][row][0]==Boolean.FALSE) {
				        	SubjectWord[k][row][0]="star.jpg";
							wordTable[k].setValueAt(iconStar, row, 0);
				        }
				        else {
				        	SubjectWord[k][row][0]=Boolean.FALSE;
							wordTable[k].setValueAt(Boolean.FALSE, row, 0);
				        }
			        }
			    }
			});
			wordTable[i].setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
			wordTable[i].setFont(new Font("돋움체", Font.BOLD, 20));
			wordTable[i].setBackground(new Color(wordColor[0]));
			wordTable[i].setRowHeight(20);
			scroll_table[i] = new JScrollPane(wordTable[i]);
		    scroll_table[i].setVisible(false);
		    scroll_table[i].setBounds(100,100,400,400);
	        this.add(scroll_table[i]);
		}
		
		//버퍼 리더로 기존에 있는 값 불러와서 배열에 넣어주기
		int wordcnt=0;
		int subjectcnt=0;
		line = br.readLine();
		count = Integer.parseInt(line);
		cntSubject=count;
		for(int i=0;i<count;i++) 
			Subjects[i].setText(br.readLine());
		while(true) {
			line = br.readLine();
			if (line==null) break;
			count = Integer.parseInt(line);
			for(int i=0;i<count;i++) {
			line = br.readLine();
			String[] wordline = line.split(" ");
			if(wordline[0].equals("star.jpg")) {
				SubjectWord[subjectcnt][i][0]="star.jpg";
				wordTable[subjectcnt].setValueAt(iconStar, i, 0);
			}
	            	else SubjectWord[subjectcnt][i][0]=Boolean.FALSE;
			    	SubjectWord[subjectcnt][i][1]=wordline[1];
			    	SubjectWord[subjectcnt][i][2]=wordline[2];
			    	SubjectWord[subjectcnt][i][3]=Boolean.FALSE;
		 	}
			SubjectWordCnt[subjectcnt]+=count;
		 	subjectcnt++;
        	}
		
		
		subjectText.setVisible(false);
		subjectText.setFont(new Font("Comic Sans", Font.BOLD, 20));
		
		testButton.setFont(new Font("Comic Sans", Font.BOLD, 30));
		testButton.setVisible(false);
		testButton.setBounds(50,20,100,50);

		selectAll.setHorizontalAlignment(JCheckBox.LEFT);
		selectYN.setText("All/None");
		selectBox.add(selectYN);
		selectBox.add(selectAll);
		selectBox.setVisible(false);
		selectBox.setBackground(new Color(wordColor[0]));
		selectBox.setBounds(388,80,94,20);
		selectBox.setBorder(BorderFactory.createEmptyBorder(-5, 0, 0, 0));
		
		deleteSelectedWords.setFont(new Font("Comic Sans", Font.BOLD, 30));
		deleteSelectedWords.setVisible(false);
		deleteSelectedWords.setBounds(170,20,100,50);

		addWord.setFont(new Font("Comic Sans", Font.BOLD, 30));
		addWord.setVisible(false);
		addWord.setBounds(290,20,100,50);

		addSubjectButton.setBounds(50+330*((cntSubject)%2),100+(cntSubject)/2*150,200,100);
		addSubjectButton.setFont(new Font("Comic Sans", Font.BOLD, 50));
		addSubjectButton.setVisible(false);
		addSubjectButton.setBackground(new Color(0x14A989));
		
		startButton.setBounds(225,400,180,100);
		startButton.setFocusable(false);
		startButton.setVerticalTextPosition(JButton.BOTTOM);
		startButton.setFont(new Font("Comic Sans", Font.BOLD, 30));
		startButton.setBackground(new Color(0x14A989));
		
		testButton.addActionListener(e -> {
			for(int i=0;i<SubjectWordCnt[currentSubject];i++) {
				if((Boolean)SubjectWord[currentSubject][i][3]==true) {
					test testFrame;
					try {
						testFrame = new test();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					break;
				}
				else if(i==SubjectWordCnt[currentSubject]-1) {
					JOptionPane.showMessageDialog(this,"테스트를 할 단어를 선택해주세요."); 
				}
			}
			
		});
		
		//새로운 주제 추가하기 버튼
		addSubjectButton.addActionListener(e -> {
			addSubjectButton.setVisible(false);
			for(int i=0;i<cntSubject;i++) {
				Subjects[i].setEnabled(false);
				deleteSubjects[i].setEnabled(false);
			}
			if(cntSubject<6) {
				Subjects[cntSubject].setVisible(false);
				subjectText.setBounds(50+330*((cntSubject)%2),100+(cntSubject)/2*150,200,100);
				subjectText.setVisible(true);
				if(cntSubject==6) //5개가 다 차면 더 이상 추가 못하게 막음
					addSubjectButton.setVisible(false);
			}
		});
		
		for(int i=0;i<6;i++) {
			int k=i;
			Subjects[i].addActionListener(e -> { //각 주제인 Subjects[i]가 눌렸을 때 
				currentSubject=k;
				/*try {
					System.out.println(SubjectWord[currentSubject][0][0]);
					tts((String) SubjectWord[currentSubject][0][0]);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}*/
				startButton.setVisible(true);
				testButton.setVisible(true);
				deleteSelectedWords.setVisible(true);
				addWord.setVisible(true);
				scroll_table[currentSubject].setVisible(true);
				for(int j=0;j<6;j++) {
					Subjects[j].setVisible(false);
					deleteSubjects[j].setVisible(false);
				}
				addSubjectButton.setVisible(false);
				selectBox.setVisible(true);
			});
			deleteSubjects[i].addActionListener(e -> { //옆에 있는 조그마한 꼽표로 해당 주제를 다 지울 때
				currentSubject=k;
				int a=JOptionPane.showConfirmDialog(this,"해당 주제 및 주제 안에 있는 단어를 모두 지우시겠습니까?"); 
				if(a==JOptionPane.YES_OPTION){  
					for(int l=currentSubject;l<5;l++) {
						Subjects[l].setText(Subjects[l+1].getText());
						for(int j=0;j<Math.max(SubjectWordCnt[l],SubjectWordCnt[l+1]);j++) {
							SubjectWord[l][j]=SubjectWord[l+1][j];
						}
					}
					cntSubject-=1;
					Subjects[cntSubject].setVisible(false);
					deleteSubjects[cntSubject].setVisible(false);
					addSubjectButton.setBounds(50+330*((cntSubject)%2),100+(cntSubject)/2*150,200,100);
					if(cntSubject<6)
						addSubjectButton.setVisible(true);
				}
			});
			
		}
		//단어 전체 선택 체크박스
		selectAll.addActionListener(e -> {
			for(int i=0;i<SubjectWordCnt[currentSubject];i++) {
				if(selectAll.isSelected())
					SubjectWord[currentSubject][i][3]=true;
				else
					SubjectWord[currentSubject][i][3]=false;
			}
			scroll_table[currentSubject].setVisible(false);
			scroll_table[currentSubject].setVisible(true);
			
		});
		//체크된 단어 삭제하기
		deleteSelectedWords.addActionListener(e -> {
			int cnt=0;
			int tmp = SubjectWordCnt[currentSubject];
			for(int i=0;i<tmp;i++) {
				while(cnt< tmp&& (boolean)SubjectWord[currentSubject][cnt][3]==true ) {
					SubjectWordCnt[currentSubject]-=1;
					cnt++;
				}
				SubjectWord[currentSubject][i][0]=SubjectWord[currentSubject][cnt][0];
				SubjectWord[currentSubject][i][1]=SubjectWord[currentSubject][cnt][1];
				SubjectWord[currentSubject][i][2]=SubjectWord[currentSubject][cnt][2];
				SubjectWord[currentSubject][i][3]=SubjectWord[currentSubject][cnt][3];
				cnt++;
			}
			if (cnt==SubjectWordCnt[currentSubject]) {
				JOptionPane.showMessageDialog(this,"삭제할 단어를 선택해주세요."); 
			}
			scroll_table[currentSubject].setVisible(false);
			scroll_table[currentSubject].setVisible(true);
		});
		
		//메인화면으로 가기
		startButton.addActionListener(e -> {
			startButton.setBounds(510,20,70,50);
			startButton.setText("<-");
			startButton.setVisible(false);
			testButton.setVisible(false);
			deleteSelectedWords.setVisible(false);
			addWord.setVisible(false);
			selectBox.setVisible(false);
			selectAll.setSelected(false);
			for(int i=0;i<cntSubject;i++) {
				scroll_table[i].setVisible(false);
				Subjects[i].setVisible(true);
				deleteSubjects[i].setVisible(true);
			}
			if(cntSubject<6)
				addSubjectButton.setVisible(true);
		});
		
		
		// 단어 추가 버튼 만들기
		addWord.addActionListener(e -> {
		  JTextField WordField = new JTextField(10);
	      JTextField MeanField = new JTextField(10);

	      JPanel myPanel = new JPanel();
	      myPanel.add(new JLabel("단어:"));
	      myPanel.add(WordField);
	      myPanel.add(Box.createHorizontalStrut(20));
	      myPanel.add(new JLabel("뜻:"));
	      myPanel.add(MeanField);
	      
	      int result = JOptionPane.showConfirmDialog(null, myPanel, "입력할 단어와 뜻 입력", JOptionPane.OK_CANCEL_OPTION);
	      if (result == JOptionPane.OK_OPTION) {
	        String Word = WordField.getText();
			String Mean = MeanField.getText();
			if(Word.equals("") || Mean.equals(""))
				JOptionPane.showMessageDialog(this,"제대로 입력해 주세요."); 
			else {
				SubjectWord[currentSubject][SubjectWordCnt[currentSubject]][0]=Boolean.FALSE;
				SubjectWord[currentSubject][SubjectWordCnt[currentSubject]][1]=Word;
				SubjectWord[currentSubject][SubjectWordCnt[currentSubject]][2]=Mean;
				SubjectWord[currentSubject][SubjectWordCnt[currentSubject]++][3]=Boolean.FALSE;
				scroll_table[currentSubject].setVisible(false);
				scroll_table[currentSubject].setVisible(true);
			}
	      }
		});
		
        //TextField에 추가 할 주제의 이름을 입력받았을 때, 그 이름을 가진 주제 버튼 만들기
		subjectText.addActionListener(new ActionListener() {
		    @Override
		    public void actionPerformed(ActionEvent e) {
		    	if(subjectText.getText().equals("")) {
		    		;
		    	}
		    	else {
		    		cntSubject++;
		    		tempSubjectName=subjectText.getText();
			    	Subjects[cntSubject-1].setVisible(true);
			    	Subjects[cntSubject-1].setText(tempSubjectName);
			    	deleteSubjects[cntSubject-1].setVisible(true);
			    	subjectText.setText("");
					addSubjectButton.setBounds(50+330*((cntSubject)%2),100+(cntSubject)/2*150,200,100);
		    	}
		    	if(cntSubject!=6)
		    		addSubjectButton.setVisible(true);
				subjectText.setVisible(false);
				for(int i=0;i<cntSubject;i++) {
					Subjects[i].setEnabled(true);
					deleteSubjects[i].setEnabled(true);
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
        /*JLabel label1 = new JLabel();
        label1.setIcon(iconNote);
        label1.setBounds(0,0,640,650);
        label1.setVisible(true);
        this.add(label1);*/
        this.add(startButton);
        this.add(addSubjectButton);
        this.add(deleteSelectedWords);
        this.add(subjectText);
        this.add(testButton);
        this.add(addWord);
        this.add(selectBox);
        
        
        br.close();
        // 창 닫을 때 배열 안에 있는 값들을 text 파일에 저장
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
		for(int i=0;i<cntSubject;i++) {
		 	fw.write(Subjects[i].getText()+"\n");
		}
		for(int i=0;i<cntSubject;i++) {
			fw.write(SubjectWordCnt[i]+"\n");
			for(int j=0;j<SubjectWordCnt[i];j++) {
				fw.write(SubjectWord[i][j][0]+" "+SubjectWord[i][j][1]+" "+SubjectWord[i][j][2]+"\n");
			}
		}
		fw.close();
    }
	void tts(String subjectWord)throws Exception {
		try (TextToSpeechClient textToSpeechClient = TextToSpeechClient.create()) {
			SynthesisInput input = SynthesisInput.newBuilder().setText(subjectWord).build();
			
			SynthesizeSpeechResponse response =textToSpeechClient.synthesizeSpeech(input, voice, audioConfig);
			ByteString audioContents = response.getAudioContent();
			try (OutputStream out = new FileOutputStream("output.mp3")) {
				out.write(audioContents.toByteArray());
				System.out.println("WOW");
			}
		}
	}
}
