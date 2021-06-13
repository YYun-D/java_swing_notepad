package projectTTS;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.sound.sampled.DataLine.Info;
import javax.swing.*;

import com.google.api.gax.rpc.ClientStream;
import com.google.api.gax.rpc.ResponseObserver;
import com.google.api.gax.rpc.StreamController;
import com.google.cloud.speech.v1.RecognitionConfig;
import com.google.cloud.speech.v1.SpeechClient;
import com.google.cloud.speech.v1.SpeechRecognitionAlternative;
import com.google.cloud.speech.v1.StreamingRecognitionConfig;
import com.google.cloud.speech.v1.StreamingRecognitionResult;
import com.google.cloud.speech.v1.StreamingRecognizeRequest;
import com.google.cloud.speech.v1.StreamingRecognizeResponse;
import com.google.cloud.texttospeech.v1.*;
import com.google.protobuf.ByteString;

public class MyFrame extends JFrame{
	static String str;
	BufferedReader br = new BufferedReader(new FileReader("word.txt"));
	static Object[][][] SubjectWord = new Object[6][][];
	JButton[] Subjects = new JButton[6];
	JButton[] deleteSubjects = new JButton[6];
	int cntSubject=1,count;
	static int currentSubject;
	String tempSubjectName;
	String line;
	Object[] columnNames = {"중요도","단어","뜻","선택"};
	Object[] voice = {"확인","취소","음성인식"};
	JTable[] wordTable = new JTable[6];
	static int[] wordColor= {0xFFFF66,0xCCFF00,0x50BFE6,0xFD5B78,0xFF6EFF,0xAF6E4D};
	JScrollPane[] scroll_table = new JScrollPane[6];
	
	static int[] SubjectWordCnt= {0,0,0,0,0,0};
	

	MyFrame() throws IOException{
		ImageIcon icon2 = new ImageIcon("noteIcon.png");
		ImageIcon iconX = new ImageIcon("XX.png");
		ImageIcon iconNote = new ImageIcon("img.png");
		ImageIcon iconStar = new ImageIcon("ic_star.png");
		ImageIcon startImg = new ImageIcon("hello.png");
		ImageIcon categoriesImg = new ImageIcon("categories.png");	//과목 선택 화면 background image
		ImageIcon subjectImg = new ImageIcon("subject.png");	//과목 진입 후 화면 background image
		JButton startButton = new JButton("START");  	//처음 start 및 뒤로 가는 버튼
		JButton addSubjectButton = new JButton("+"); 	//주제 추가 버튼
		JButton testButton = new JButton("TEST");    	//해당 과목에 있는 것들 테스트 버튼
		JButton deleteSelectedWords = new JButton("-");	//해당 주제 삭제 버튼
		JButton addWord = new JButton("+");         	//해당 주제 테이블에 새로운 단어 추가 버튼
		JCheckBox selectAll = new JCheckBox(); 
		JCheckBox selectImportant = new JCheckBox(); 
		JTextField subjectText = new JTextField();     //주제 추가할 때 쓰는 텍스트필드
		JPanel selectBox= new JPanel();
		JPanel selectBox1= new JPanel();
		JLabel selectYN=new JLabel();
		JLabel selectYN1=new JLabel();
		JLabel categoriesLabel = new JLabel();		//JLabel for 과목 선택 화면 background
		JLabel subjectLabel = new JLabel();		//JLabel for 과목 진입 후 화면 background
		JLabel catTitleLabel = new JLabel("");		//주제 선택 화면-제목
		JLabel catDescriptionLabel = new JLabel("");	//주제 선택 화면-설명
		JLabel startPageLabel = new JLabel("");
//		JLabel subjectLabel=new JLabel("");
		
		startPageLabel.setIcon(startImg);
		startPageLabel.setBounds(0,0,480,853);
		startPageLabel.setVisible(true);
		
		JPanel startPagePanel = new JPanel();
		startPagePanel.setVisible(false);
		startPagePanel.setBounds(0,-10,480,853);
		this.add(startPagePanel);
		startPagePanel.add(categoriesLabel);
		
		//과목 선택 화면 background image 삽입 위한 패널 생성 및 사이즈 설정
		categoriesLabel.setIcon(categoriesImg);
		categoriesLabel.setBounds(0,0,480,853);
		categoriesLabel.setVisible(true);

		JPanel categoriesPanel = new JPanel();
		categoriesPanel.setVisible(false);	//생성 시엔 visibility false, 시각화할 때만 true 설정
		categoriesPanel.setBounds(0,-10,480,853);
		this.add(categoriesPanel);
		categoriesPanel.add(categoriesLabel);
		
		//----------------------------//
		
		//해당 과목 화면 background image 삽입 위한 패널 생성 및 사이즈 설정
		subjectLabel.setIcon(subjectImg);
		subjectLabel.setBounds(0,0,480,853);
		subjectLabel.setVisible(true);
		
		JPanel subjectPanel = new JPanel();
		subjectPanel.setVisible(false);
		subjectPanel.setBounds(0,-10,480,853);
		this.add(subjectPanel);
		subjectPanel.add(subjectLabel);
		
		//과목 선택 화면 텍스트 설정
		catTitleLabel.setVisible(false);
		catTitleLabel.setFont(new Font("Comic Sans",Font.BOLD ,30));		//커스텀 폰트 어떻게?
		catTitleLabel.setBounds(50,50,300,100);
		categoriesLabel.add(catTitleLabel);		//배경 위에 텍스트를 얹어주기 위함
		
		catDescriptionLabel.setVisible(false);
		catDescriptionLabel.setFont(new Font("Apple SD Gothic Neo", Font.PLAIN,30));
		catDescriptionLabel.setBounds(50,70,100,100);
		categoriesLabel.add(catDescriptionLabel);
		
		//SubjectWord라는 각 주제 별 단어 저장용 배열 생성
		for(int i=0;i<6;i++) 
			SubjectWord[i]=new Object[100][4];
		//Subjects라는 각 주제 별 메인 화면에서 클릭할 버튼 생성
		for(int i=0;i<6;i++) {
			Subjects[i] = new JButton();
			Subjects[i].setVisible(false);
			Subjects[i].setBackground(new Color(wordColor[i]));
			Subjects[i].setFont(new Font("Comic Sans", Font.BOLD, 30));
			Subjects[i].setBounds(36,286+(i)*102,408,84);
			
//			this.add(Subjects[i]);
			categoriesLabel.add(Subjects[i]);
//			subjectLabel.add(Subjects[i]);
			
			deleteSubjects[i]=new JButton();
			deleteSubjects[i].setVisible(false);
			deleteSubjects[i].setBackground(new Color(wordColor[i]));
			deleteSubjects[i].setIcon(iconX);
			deleteSubjects[i].setBounds(440,286+(i)*102,30,30);
//			this.add(deleteSubjects[i]);
			categoriesLabel.add(deleteSubjects[i]);
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
				        else if(col == 0) {
				        	SubjectWord[k][row][0]=Boolean.FALSE;
							wordTable[k].setValueAt(Boolean.FALSE, row, 0);
				        }
			        }
			    }
			});
			wordTable[i].setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
			wordTable[i].setFont(new Font("돋움체", Font.BOLD, 15));
			wordTable[i].setBackground(new Color(wordColor[0]));
			wordTable[i].setRowHeight(40);
			scroll_table[i] = new JScrollPane(wordTable[i]);
			scroll_table[i].setVisible(false);
			scroll_table[i].setBounds(43,276,394,502);
			wordTable[i].getColumnModel().getColumn(0).setPreferredWidth(50);
			wordTable[i].getColumnModel().getColumn(1).setPreferredWidth(150);
			wordTable[i].getColumnModel().getColumn(2).setPreferredWidth(150);
			wordTable[i].getColumnModel().getColumn(3).setPreferredWidth(50);
				subjectLabel.add(scroll_table[i]);
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
	            if(wordline[0].equals("ic_star.png")) {
	            	SubjectWord[subjectcnt][i][0]="ic_star.png";
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
		
		// 시험 응시 버튼 시각적 속성 설정
		testButton.setFont(new Font("Comic Sans", Font.BOLD, 30));
		testButton.setVisible(false);
		testButton.setBounds(334,196,106,48);
		testButton.setBorderPainted(false);		//make the button transparent
		testButton.setFont(new Font("Arial", Font.BOLD, 20));
		testButton.setForeground(Color.WHITE);	//폰트 색 설정

		selectAll.setHorizontalAlignment(JCheckBox.LEFT);
		selectYN.setText("All/None");
		selectBox.add(selectYN);
		selectBox.add(selectAll);
		selectBox.setVisible(false);
		selectBox.setBackground(new Color(wordColor[0]));
		selectBox.setBounds(343,258,94,20);
		selectBox.setBorder(BorderFactory.createEmptyBorder(-5, 0, 0, 0));
		
//		selectImportant.setHorizontalAlignment(JCheckBox.LEFT);
//		selectYN1.setText("All/None");
//		selectBox1.add(selectYN1);
//		selectBox1.add(selectImportant);
//		selectBox1.setVisible(false);
//		//selectBox1.setBackground(new Color(wordColor[0]));
//		selectBox1.setBounds(200,200,94,20);
//		selectBox1.setBorder(BorderFactory.createEmptyBorder(-5, 0, 0, 0));
		
		//단어 삭제 버튼 시각적 속성
		deleteSelectedWords.setFont(new Font("Comic Sans", Font.BOLD, 25));
		deleteSelectedWords.setVisible(false);
		deleteSelectedWords.setBounds(100,187,65,65);
		deleteSelectedWords.setBorderPainted(false);

		//단어 추가 버튼 시각적 속성
		addWord.setFont(new Font("Comic Sans", Font.BOLD, 25));
		addWord.setVisible(false);
		addWord.setBounds(38,186,65,65);
		addWord.setBorderPainted(false);

		addSubjectButton.setBounds(36,286+(cntSubject)*102,408,84);
		addSubjectButton.setFont(new Font("Comic Sans", Font.BOLD, 50));
		addSubjectButton.setVisible(false);
		addSubjectButton.setBackground(new Color(0x14A989));
		
		startButton.setBounds(225,400,180,100);
		startButton.setFocusable(false);
		startButton.setVerticalTextPosition(JButton.BOTTOM);
		startButton.setFont(new Font("Comic Sans", Font.BOLD, 30));
		startButton.setBackground(new Color(0x14A989));
		
		// 테스트 버튼
		testButton.addActionListener(e -> {
			for(int i=0;i<SubjectWordCnt[currentSubject];i++) {
				if((Boolean)SubjectWord[currentSubject][i][3]==true) {
					test testFrame;
					try {
						testFrame = new test();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (LineUnavailableException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (UnsupportedAudioFileException e1) {
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
				subjectText.setBounds(36,286+(cntSubject)*102,408,84);
				subjectText.setVisible(true);
				if(cntSubject==5) //5개가 다 차면 더 이상 추가 못하게 막음
					addSubjectButton.setVisible(false);
			}
		});
		
		for(int i=0;i<6;i++) {
			int k=i;
			
			//각 주제인 Subjects[i]가 눌렸을 때
			Subjects[i].addActionListener(e -> {
				currentSubject=k;
				
				//이전 페이지의 개체들을 모두 보이지 않게끔 설정
				categoriesPanel.setVisible(false);
				categoriesLabel.setVisible(false);
				catTitleLabel.setVisible(false);
				catDescriptionLabel.setVisible(false);
				
				//해당 과목 배경 이미지 visualize
				subjectPanel.setVisible(true);
				subjectLabel.setVisible(true);
				subjectLabel.add(startButton);
				
//				subjectLabel.add(subjectText);
//				subjectText.setVisible(true);
//				subjectText.setBounds(36,60,408,84);
//				subjectText.setBorderPainted(false);
				
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
				selectBox1.setVisible(true);
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
					SubjectWordCnt[cntSubject]=0;
					addSubjectButton.setBounds(36,286+(cntSubject)*102,408,84);
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
		//중요도 단어 전체 선택 체크박스
		selectImportant.addActionListener(e -> {
			for(int i=0;i<SubjectWordCnt[currentSubject];i++) {
				if(selectImportant.isSelected()) {
					if(!(boolean)SubjectWord[currentSubject][i][0].equals(false)) {
						SubjectWord[currentSubject][i][3]=true;
					}
				}
				else
					if(!(boolean)SubjectWord[currentSubject][i][0].equals(false)) 
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
			
			startButton.setBounds(41,41,64,64);
			startButton.setBorderPainted(false);	//make startbutton transparent
			
			startButton.setText("");
			startButton.setVisible(false);
			testButton.setVisible(false);
			deleteSelectedWords.setVisible(false);
			addWord.setVisible(false);
			selectBox.setVisible(false);
			selectBox1.setVisible(false);
			selectAll.setSelected(false);
			
			//과목 선택 페이지에서 visualize 될 것들
			categoriesPanel.setVisible(true);
			categoriesLabel.setVisible(true);
			catTitleLabel.setVisible(true);
			catDescriptionLabel.setVisible(true);
			
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
			int result = JOptionPane.showOptionDialog(null, myPanel, "입력할 단어와 뜻 입력", JOptionPane.OK_CANCEL_OPTION,JOptionPane.PLAIN_MESSAGE, iconStar, voice, null);
  
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
		  	if (result == 2) { // 음성인식이 골라졌을 때
		  		SubjectWord[currentSubject][SubjectWordCnt[currentSubject]][0]=Boolean.FALSE;
		  		str=null;
		  		String tmp1=null;
		  		String tmp2=null;
		  		int tmp3=-1;
		  		JOptionPane.showMessageDialog(myPanel, "OK를 눌러 단어 음성인식 시작", null, 1);
				try {
					while(str==null) {
						streamingMicRecognize(0);
						if(str==null) tmp3= JOptionPane.showConfirmDialog(this, "제대로 인식이 안됐습니다. 다시 하시겠습니까?"); 
						else tmp3=0;
						if(tmp3!=0) return;
					}
					tmp1=str;
				} catch (Exception e1) {
				    e1.printStackTrace();
				}
				str=null;
				JOptionPane.showMessageDialog(myPanel, "OK를 눌러 뜻(한국어) 음성인식 시작", null, 1);
				try {
					while(str==null) {
						streamingMicRecognize(1);
						if(str==null) tmp3= JOptionPane.showConfirmDialog(this, "제대로 인식이 안됐습니다. 다시 하시겠습니까?"); 
						else tmp3=0;
						if(tmp3!=0) return;
					}
					tmp2=str;
				} catch (Exception e1) {
				    e1.printStackTrace();
				}
		  		SubjectWord[currentSubject][SubjectWordCnt[currentSubject]][0]=Boolean.FALSE;
				SubjectWord[currentSubject][SubjectWordCnt[currentSubject]][1]=tmp1;
				SubjectWord[currentSubject][SubjectWordCnt[currentSubject]][2]=tmp2;
				SubjectWord[currentSubject][SubjectWordCnt[currentSubject]++][3]=Boolean.FALSE;
				scroll_table[currentSubject].setVisible(false);
				scroll_table[currentSubject].setVisible(true);
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
					addSubjectButton.setBounds(36,286+(cntSubject)*102,408,84);
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
//        this.setSize(480,853);
				this.setSize(480,853);	//창 크기 설정, 16:9 종횡비
        this.setLayout(null);
        this.setVisible(true);
//        this.getContentPane().setBackground(new Color(0xf8b195));
        /*JLabel label1 = new JLabel();
        label1.setIcon(iconNote);
        label1.setBounds(0,0,640,650);
        label1.setVisible(true);
        this.add(label1);*/
				startPagePanel.setVisible(true);
        startPageLabel.add(startButton);
        
        
        categoriesLabel.add(addSubjectButton);
        subjectLabel.add(deleteSelectedWords);
        categoriesLabel.add(subjectText);

		//	배경 이미지 위에 버튼 추가
				subjectLabel.add(testButton);
				subjectLabel.add(addWord);
				subjectLabel.add(selectBox);
				subjectLabel.add(selectBox1);
				
//				subjectLabel.add(subjectText);
//				subjectText.setVisible(true);
//				subjectText.setBounds(36,60,408,84);
//				subjectText.setBorderPainted(false);
        
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
	public static void streamingMicRecognize(int lan) throws Exception {
		
    	ResponseObserver<StreamingRecognizeResponse> responseObserver = null;
        try (SpeechClient client = SpeechClient.create()) {

            responseObserver =
                new ResponseObserver<StreamingRecognizeResponse>() {
                    ArrayList<StreamingRecognizeResponse> responses = new ArrayList<>();

                    public void onStart(StreamController controller) {}

                    public void onResponse(StreamingRecognizeResponse response) {
                        responses.add(response);
                    }

                    public void onComplete() {
                        for (StreamingRecognizeResponse response : responses) {
                            StreamingRecognitionResult result = response.getResultsList().get(0);
                            SpeechRecognitionAlternative alternative = result.getAlternativesList().get(0);
                            System.out.printf("Transcript : %s\n", alternative.getTranscript());
                            str=alternative.getTranscript();
                        }
                    }

                    public void onError(Throwable t) {
                        System.out.println(t);
                    }
                };

	        ClientStream<StreamingRecognizeRequest> clientStream =
	                client.streamingRecognizeCallable().splitCall(responseObserver);
	
	        
	        RecognitionConfig recognitionConfig;
	        if(lan==0) {
		        recognitionConfig=RecognitionConfig.newBuilder()
		                        .setEncoding(RecognitionConfig.AudioEncoding.LINEAR16)
		                        .setLanguageCode("en-US")
		                        .setSampleRateHertz(16000)
		                        .build();
	        }
	        else {
		        recognitionConfig=RecognitionConfig.newBuilder()
		                        .setEncoding(RecognitionConfig.AudioEncoding.LINEAR16)
		                        .setLanguageCode("ko-KR")
		                        .setSampleRateHertz(16000)
		                        .build();
	        }
	        StreamingRecognitionConfig streamingRecognitionConfig =
	                StreamingRecognitionConfig.newBuilder().setConfig(recognitionConfig).build();
	
	        StreamingRecognizeRequest request =
	                StreamingRecognizeRequest.newBuilder()
	                        .setStreamingConfig(streamingRecognitionConfig)
	                        .build(); // The first request in a streaming call has to be a config
	
	        clientStream.send(request);
	        // SampleRate:16000Hz, SampleSizeInBits: 16, Number of channels: 1, Signed: true,
	        // bigEndian: false
	        AudioFormat audioFormat = new AudioFormat(16000, 16, 1, true, false);
	        DataLine.Info targetInfo =
	                new Info(
	                        TargetDataLine.class,
	                        audioFormat); // Set the system information to read from the microphone audio stream
	
	        if (!AudioSystem.isLineSupported(targetInfo)) {
	            System.out.println("Microphone not supported");
	            System.exit(0);
	        }
	        // Target data line captures the audio stream the microphone produces.
	        TargetDataLine targetDataLine = (TargetDataLine) AudioSystem.getLine(targetInfo);
	        targetDataLine.open(audioFormat);
	        targetDataLine.start();
	        System.out.println("Start speaking");
	        long startTime = System.currentTimeMillis();
	        // Audio Input Stream
	        AudioInputStream audio = new AudioInputStream(targetDataLine);
	        while (true) {
	            long estimatedTime = System.currentTimeMillis() - startTime;
	            byte[] data = new byte[6400];
	            audio.read(data);
	            if (estimatedTime > 3000) { // 6 seconds
	                System.out.println("Stop speaking.");
	                targetDataLine.stop();
	                targetDataLine.close();
	                break;
	            }
	            request = StreamingRecognizeRequest.newBuilder().setAudioContent(ByteString.copyFrom(data)).build();
	            clientStream.send(request);
	        }
	    } catch (Exception e) {
	        System.out.println(e);
	    }
	    responseObserver.onComplete();
	}
	
}
