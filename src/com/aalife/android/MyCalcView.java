package com.aalife.android;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

public class MyCalcView extends LinearLayout {
	private EditText calcText = null;
	public String resultText = "";

	public MyCalcView(Context context) {
		super(context);
		
		LayoutInflater inflater = LayoutInflater.from(context);
		View popView = inflater.inflate(R.layout.layout_calculator, new LinearLayout(context), false);
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		popView.setLayoutParams(params);

		calcText = (EditText) popView.findViewById(R.id.calctext);

		Button btn1 = (Button) popView.findViewById(R.id.button1);
		btn1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				doVibrator();
				if (!hasResult()) {
					String s = calcText.getText().toString();
					calcText.setText(s + "1");
					setGuangBiao();
				}
			}
		});
		Button btn2 = (Button) popView.findViewById(R.id.button2);
		btn2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				doVibrator();
				if (!hasResult()) {
					String s = calcText.getText().toString();
					calcText.setText(s + "2");
					setGuangBiao();
				}
			}
		});
		Button btn3 = (Button) popView.findViewById(R.id.button3);
		btn3.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				doVibrator();
				if (!hasResult()) {
					String s = calcText.getText().toString();
					calcText.setText(s + "3");
					setGuangBiao();
				}
			}
		});
		Button btn4 = (Button) popView.findViewById(R.id.button4);
		btn4.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				doVibrator();
				if (!hasResult()) {
					String s = calcText.getText().toString();
					calcText.setText(s + "4");
					setGuangBiao();
				}
			}
		});
		Button btn5 = (Button) popView.findViewById(R.id.button5);
		btn5.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				doVibrator();
				if (!hasResult()) {
					String s = calcText.getText().toString();
					calcText.setText(s + "5");
					setGuangBiao();
				}
			}
		});
		Button btn6 = (Button) popView.findViewById(R.id.button6);
		btn6.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				doVibrator();
				if (!hasResult()) {
					String s = calcText.getText().toString();
					calcText.setText(s + "6");
					setGuangBiao();
				}
			}
		});
		Button btn7 = (Button) popView.findViewById(R.id.button7);
		btn7.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				doVibrator();
				if (!hasResult()) {
					String s = calcText.getText().toString();
					calcText.setText(s + "7");
					setGuangBiao();
				}
			}
		});
		Button btn8 = (Button) popView.findViewById(R.id.button8);
		btn8.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				doVibrator();
				if (!hasResult()) {
					String s = calcText.getText().toString();
					calcText.setText(s + "8");
					setGuangBiao();
				}
			}
		});
		Button btn9 = (Button) popView.findViewById(R.id.button9);
		btn9.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				doVibrator();
				if (!hasResult()) {
					String s = calcText.getText().toString();
					calcText.setText(s + "9");
					setGuangBiao();
				}
			}
		});
		Button btn0 = (Button) popView.findViewById(R.id.button0);
		btn0.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				doVibrator();
				if (!hasResult()) {
					String s = calcText.getText().toString();
					calcText.setText(s + "0");
					setGuangBiao();
				}
			}
		});
		//点按钮
		Button btnDian = (Button) popView.findViewById(R.id.buttondian);
		btnDian.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				doVibrator();
				if (!hasResult()) {
					String s = calcText.getText().toString();
					if (hasDian(s)) {
						calcText.setText(s + ".");
					} else {
						return;
					}
					setGuangBiao();
				}
			}
		});
		//删除按钮
		Button btnClear = (Button) popView.findViewById(R.id.buttonclear);
		btnClear.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				doVibrator();
				calcText.setText("");
			}
		});
		Button btnBack = (Button) popView.findViewById(R.id.buttonback);
		btnBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				doVibrator();
				String s = calcText.getText().toString();
				if (s.length() > 0) {
					s = s.substring(0, s.length() - 1);
					calcText.setText(s);
					setGuangBiao();
				}
			}
		});
		//运算加
		Button btnJia = (Button) popView.findViewById(R.id.buttonjia);
		btnJia.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				doVibrator();
				String s = calcText.getText().toString();
				if (hasResult()) {
					s = UtilityHelper.formatDouble(Math.abs(Double.parseDouble(resultText)), "#.######");
					calcText.setText(s);
				}				
				if (isNumber(s)) {
					calcText.setText(s + "+");
				} else {
					return;
				}
				setGuangBiao();
			}
		});
		//运算减
		Button btnJian = (Button) popView.findViewById(R.id.buttonjian);
		btnJian.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				doVibrator();
				String s = calcText.getText().toString();
				if (hasResult()) {
					s = UtilityHelper.formatDouble(Math.abs(Double.parseDouble(resultText)), "#.######");
					calcText.setText(s);
				}
				if (isNumber(s)) {
					calcText.setText(s + "-");
				} else {
					return;
				}
				setGuangBiao();
			}
		});
		//运算乘
		Button btnChen = (Button) popView.findViewById(R.id.buttonchen);
		btnChen.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				doVibrator();
				String s = calcText.getText().toString();
				if (hasResult()) {
					s = UtilityHelper.formatDouble(Math.abs(Double.parseDouble(resultText)), "#.######");
					calcText.setText(s);
				}
				if (isNumber(s)) {
					calcText.setText(s + "×");
				} else {
					return;
				}
				setGuangBiao();
			}
		});
		//运算除
		Button btnChu = (Button) popView.findViewById(R.id.buttonchu);
		btnChu.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				doVibrator();
				String s = calcText.getText().toString();
				if (hasResult()) {
					s = UtilityHelper.formatDouble(Math.abs(Double.parseDouble(resultText)), "#.######");
					calcText.setText(s);
				}					
				if (isNumber(s)) {
					calcText.setText(s + "÷");
				} else {
					return;
				}
				setGuangBiao();
			}
		});
		//等于按钮
		Button btnDen = (Button) popView.findViewById(R.id.buttonden);
		btnDen.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				doVibrator();
				if (!hasResult()) {
					String s = calcText.getText().toString();
					if (isNumber(s)) {
						getResult(s);
					} else {
						return;
					}
					setGuangBiao();
				}
			}
		});

		this.addView(popView);
	}

	//设置震动
	private void doVibrator() {
		
	}
	
	//设置光标靠右
	private void setGuangBiao() {        
		calcText.setSelection(calcText.length());
	}

	//判断最后一位是否数字
	private Boolean isNumber(String s) {
		if (s.length() > 0) {
			s = s.substring(s.length() - 1);

			Pattern pattern = Pattern.compile("[0-9]?");
			return pattern.matcher(s).matches();
		}
		return false;
	}

	//判断能否输入点
	private Boolean hasDian(String s) {
		if (s.length() > 0) {
			Pattern pattern = Pattern.compile("[0-9]+[\\.]*[0-9]*$");
			Matcher matcher = pattern.matcher(s);
			String r = "";
			if (matcher.find()) {
				r = matcher.group(0);
			}
			if (!r.equals("")) {
				if (r.lastIndexOf('.') < 0) {
					return true;
				}
			}
		}
		return false;
	}

	//计算结果
	private void getResult(String s) {
		if (s.length() > 0) {
			Pattern pattern = Pattern.compile("[0-9\\.]+");
			Matcher matcher = pattern.matcher(s);
			List<Double> numList = new ArrayList<Double>();
			while (matcher.find()) {
				numList.add(Double.parseDouble(matcher.group()));
			}

			pattern = Pattern.compile("[\\+\\-\\×\\÷]+");
			matcher = pattern.matcher(s);
			List<String> signList = new ArrayList<String>();
			while (matcher.find()) {
				signList.add(matcher.group());
			}

			for (int i = 0; i < signList.size(); i++) {
				String sign = "+";
				if (i > 0) {
					sign = signList.get(i - 1);
				}

				if (signList.get(i).equals("×")) {
					double n1 = numList.get(i);
					double n2 = numList.get(i + 1);

					numList.remove(i);
					numList.add(i, 0.0);
					numList.remove(i + 1);
					numList.add(i + 1, n1 * n2);

					signList.remove(i);
					signList.add(i, sign);
				}
				if (signList.get(i).equals("÷")) {
					double n1 = numList.get(i);
					double n2 = numList.get(i + 1);

					numList.remove(i);
					numList.add(i, 0.0);
					numList.remove(i + 1);
					numList.add(i + 1, n1 / n2);

					signList.remove(i);
					signList.add(i, sign);
				}
			}

			for (int i = 0; i < signList.size(); i++) {
				if (signList.get(i).equals("+")) {
					double n1 = numList.get(i);
					double n2 = numList.get(i + 1);

					numList.remove(i);
					numList.add(i, 0.0);
					numList.remove(i + 1);
					numList.add(i + 1, n1 + n2);

					signList.remove(i);
					signList.add(i, "");
				}
				if (signList.get(i).equals("-")) {
					double n1 = numList.get(i);
					double n2 = numList.get(i + 1);

					numList.remove(i);
					numList.add(i, 0.0);
					numList.remove(i + 1);
					numList.add(i + 1, n1 - n2);

					signList.remove(i);
					signList.add(i, "");
				}
			}

			resultText = UtilityHelper.formatDouble(numList.get(numList.size() - 1), "#.######");
			calcText.setText(s + "=" + resultText);
		}
	}
	
	//判断是否已计算
	private Boolean hasResult() {
		String s = calcText.getText().toString();
		if (s.length() > 0) {
			if (s.lastIndexOf('=') >= 0) {
				return true;
			}			
		}

		return false;
	}
}
