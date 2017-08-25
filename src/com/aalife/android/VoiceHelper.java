package com.aalife.android;


import java.util.ArrayList;
import java.util.List;

public class VoiceHelper {	
	private String[][] numStrArr = {{"一", "1"}, {"二", "2"}, {"两", "2"}, {"三", "3"}, {"四", "4"}, {"五", "5"}, {"六", "6"}, {"七", "7"}, {"八", "8"}, {"吧", "8"}, {"九", "9"}, {"零", "0"},
		                            {"十", "10"}, {"百", "00"}, {"千", "000"}, {"万", "0000"}, {"块", "."}, {"元", "."}, {"点", "."}, {"毛", "m"}, {"角", "m"},
		                            {"0", "0s"}, {"1", "1s"}, {"2", "2s"}, {"3", "3s"}, {"4", "4s"}, {"5", "5s"}, {"6", "6s"}, {"7", "7s"}, {"8", "8s"}, {"9", "9s"}, {".", ".s"}};

	public VoiceHelper() {
		
	}
	
	public String[] splitWords(String words) {				
		List<String> numList = new ArrayList<String>();
		Boolean numFlag = false;
		Boolean flag = false;
		int count = 0;
		int startIndex = 0;
		String[] wordsArray = words.split("");
		
		for(int i=1; i<wordsArray.length; i++) {
            for(int j=0; j<numStrArr.length; j++) {
	    		if(wordsArray[i].equals(numStrArr[j][0])) {	
	    			numFlag = true;
	    			numList.add(numStrArr[j][1]);
	    			break;
	    		} else {
	    			numFlag = false;
	    		}
	    	}
            
            if(numFlag) {
            	if(!flag) {
            		flag = true;
            		count++;
            	}            	
            } else {
            	flag = false;
            }

			if(count == 1 && startIndex == 0) {
				startIndex = i - 1;
			}
			
            if(count == 2) {
            	int size = numList.size()-1;
            	for(int k = 0; k < size; k++) {
            		numList.remove(0);
            	}
            	count = 1;
            	startIndex = i - 1;
            }
		}

		String[] result = { "", "" };
		if(numList.size() > 0) {
            numList = fixNumber(numList);
    		result[0] = words.substring(0, startIndex);
    		result[1] = transString(numList);
		}
		
		return result;
	}
	
	public List<String> fixNumber(List<String> numList) {
		if(numList.size() == 2 && numList.get(0).equals("10") && numList.get(1).equals(".")) {
			numList.add(1, "0");
		}
		
		if(numList.get(numList.size()-1).equals(".")) {
		    numList.remove(numList.size()-1);
		}
		
		if(numList.size() >= 2 && numList.get(0).equals("10") && numList.get(1).equals(".")) {
			numList.add(1, "0");
		}
				
		if(numList.size() == 3 && numList.get(1).equals("0000") && !numList.get(2).equals("0")) {
			numList.add(3, "000");
		}
		if(numList.size() == 3 && numList.get(1).equals("000") && !numList.get(2).equals("0")) {
			numList.add(3, "00");
		}
		if(numList.size() == 5 && numList.get(1).equals("0000") && !numList.get(2).equals("0") && numList.get(3).equals("000") && !numList.get(4).equals("0")) {
			numList.set(3, "000");
			numList.add(5, "00");
		}
		
		return numList;
	}
	
	public String transString(List<String> numList) {
		double value = 0;
		boolean flag = false;
		String result = "";
		
		for(int i=0; i<numList.size(); i+=2) {
			String num = numList.get(i);
			String str = (i==numList.size()-1) ? " " : numList.get(i+1);
			
			if(num.equals("10")) num = "1";
			if(str.equals("10")) str = "0";
			
			if(num.equals("m")) {
				num = "";
			}
			if(str.equals("m")) {
				str = num;
				num = ".";
				if(flag) num = "";
			}
			
			if(num.lastIndexOf("s") == 1 || str.lastIndexOf("s") == 1) {
				flag = true;
				num = num.substring(0, 1);
				str = str.substring(0, 1);
			}
			
			String string = num + str;	
			if(flag) {
				result += string;
			} else {
			    value += Double.parseDouble(string);
			}
			if(!flag && string.lastIndexOf(".") != -1) {
				flag = true;
				String[] arr = String.valueOf(value).split("\\.");
				result = arr[1].equals("0") ? arr[0] + "." : String.valueOf(value);
			}
		}

		if(result.equals("")) {
			result = String.valueOf(value);
		}
		if(result.lastIndexOf(".") == result.length()-1) {
			result += "0";
		}
		
		return result;
	}
	
}
