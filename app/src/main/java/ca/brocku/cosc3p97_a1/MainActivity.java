package ca.brocku.cosc3p97_a1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.text.DecimalFormat;

/** @title COSC 3P97 A1
 * @author Geoffrey Jensen
 * Student #: 7148710
 *
 * MainActivity for Calcualtor app
 */

public class MainActivity extends AppCompatActivity {
    String currentOperand;
    String prevOperand;
    Mode mode;
    Operator selectedOperator;
    Calculator calculator;
    String correctionBuffer;
    CalculatorButton lastPressed;
    String memory;
    boolean error;
    final static int MAX_DIGITS = 36;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        calculator = new Calculator();
        mode = Mode.BASIC;
        selectedOperator = Operator.NONE;
        currentOperand = "";
        prevOperand = "";
        correctionBuffer = "";
        memory = "0";
        error = false;
        lastPressed = CalculatorButton.NONE;
        if(savedInstanceState!=null){
            currentOperand = savedInstanceState.getString("currentOperand");
            prevOperand = savedInstanceState.getString("prevOperand");
            mode = (Mode) savedInstanceState.getSerializable("mode");
            selectedOperator = (Operator) savedInstanceState.getSerializable("selectedOperator");
            correctionBuffer = savedInstanceState.getString("correctionBuffer");
            lastPressed = (CalculatorButton) savedInstanceState.getSerializable("lastPressed");
            memory = savedInstanceState.getString("memory");
            error = savedInstanceState.getBoolean("error");
        }
        setMode(mode);
        if(lastPressed == CalculatorButton.EQUALS && mode == Mode.BASIC){
            setDisplay(prevOperand);
        }
        else{
            setDisplay(currentOperand);
        }
        setSelectedOperator(selectedOperator);

        final ToggleButton tb = (ToggleButton) findViewById(R.id.mode);
        tb.setOnClickListener((v) -> {
            currentOperand = "";
            prevOperand = "";
            correctionBuffer = "";
            memory = "";
            selectedOperator = Operator.NONE;
            setSelectedOperator(selectedOperator);

            if(tb.isChecked()){
                mode = Mode.FORMULA;
                setMode(mode);
            }
            else{
                mode = Mode.BASIC;
                setMode(mode);
            }
            setDisplay(currentOperand);
        });

    }
    @Override
    protected void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putString("currentOperand", currentOperand);
        outState.putString("prevOperand", prevOperand);
        outState.putSerializable("mode", mode);
        outState.putSerializable("selectedOperator", selectedOperator);
        outState.putString("correctionBuffer", correctionBuffer);
        outState.putSerializable("lastPressed", lastPressed);
        outState.putString("memory", memory);
        outState.putBoolean("error", error);
    }

    /** AC button - All clear
     *
     * @param view
     */
    public void AC(View view) {
        prevOperand = "";
        currentOperand = "";
        error = false;
        setDisplay(currentOperand);
        selectedOperator = Operator.NONE;
        setSelectedOperator(selectedOperator);
    }

    /** C button - Clear
     *
     * @param view
     */
    public void C(View view) {
        if(mode == Mode.BASIC){
            if(lastPressed == CalculatorButton.OPERAND && currentOperand.length()>0){
                currentOperand = currentOperand.substring(0,currentOperand.length()-1);
                setDisplay(currentOperand);
            }
            else if(lastPressed == CalculatorButton.OPERATOR){
                selectedOperator = Operator.NONE;
                currentOperand = correctionBuffer;
                setSelectedOperator(selectedOperator);
            }
        }
        else if(mode == Mode.FORMULA){
            if(currentOperand.length() > 0){
                currentOperand = currentOperand.substring(0,currentOperand.length()-1);
                setDisplay(currentOperand);
            }
        }

    }

    /** Decimal button
     *
     * @param view
     */
    public void decimal(View view) {
        if(mode == Mode.BASIC){
            if(lastPressed == CalculatorButton.EQUALS){
                prevOperand = "";
                currentOperand = "";
                correctionBuffer = "";
            }
            else if(lastPressed == CalculatorButton.OPERATOR){
                correctionBuffer = currentOperand;
                currentOperand="";
            }
            int index = find(currentOperand, '.');
            if(index >= 0){
                if(index== currentOperand.length()-1){
                    currentOperand = currentOperand.substring(0, currentOperand.length()-1);
                }
                else if(index==0){
                    currentOperand = currentOperand.substring(1);
                }
                else{
                    currentOperand = currentOperand.substring(0,index) + currentOperand.substring(index+1);
                }

            }
            lastPressed = CalculatorButton.OPERAND;
            addDigit('.');
            setDisplay(currentOperand);
        }
        else if(mode == Mode.FORMULA){
            currentOperand+='.';
            setDisplay(currentOperand);
        }

    }

    /** Helper function to find if a char exists in a string. Used to find if a decimal already exists.
     *
     * @param s
     * @param k
     * @return
     */
    private int find(String s, char k){
        for(int i = 0; i < s.length(); i++){
            if(s.charAt(i) == k){
                return i;
            }
        }
        return -1;
    }


    /** Operator button event handler
     *
     * @param view
     */
    public void operator(View view){
        int id = view.getId();

        if(prevOperand.isEmpty()){
            prevOperand = currentOperand;
        }

        if(id == R.id.add){
            if(mode == Mode.BASIC){
                if(selectedOperator == Operator.NONE && lastPressed == CalculatorButton.OPERAND){
                    selectedOperator = Operator.ADD;
                }
                else{
                    basicCalculate();
                    selectedOperator = Operator.ADD;
                }
            }
            else if(mode == Mode.FORMULA){
                currentOperand+='+';
            }
        }
        else if(id == R.id.subtract){
            if(mode == Mode.BASIC){
                if(selectedOperator == Operator.NONE && lastPressed == CalculatorButton.OPERAND){
                    selectedOperator = Operator.SUBTRACT;
                }
                else{
                    basicCalculate();
                    selectedOperator = Operator.SUBTRACT;
                }
            }
            else if(mode == Mode.FORMULA){
                currentOperand+='-';
            }

        }
        else if(id == R.id.divide){
            if(mode == Mode.BASIC){
                if(selectedOperator == Operator.NONE && lastPressed == CalculatorButton.OPERAND){
                    selectedOperator = Operator.DIVIDE;
                }
                else{
                    basicCalculate();
                    selectedOperator = Operator.DIVIDE;
                }
            }
            else if(mode == Mode.FORMULA){
                currentOperand+='/';
            }

        }
        else if(id == R.id.multiply){
            if(mode == Mode.BASIC){
                if(selectedOperator == Operator.NONE && lastPressed == CalculatorButton.OPERAND){
                    selectedOperator = Operator.MULTIPLY;
                }
                else{
                    basicCalculate();
                    selectedOperator = Operator.MULTIPLY;
                }
            }
            else if(mode == Mode.FORMULA){
                currentOperand+='*';
            }

        }
        if(mode == Mode.FORMULA){
            setDisplay(currentOperand);
        }
        setSelectedOperator(selectedOperator);
        lastPressed = CalculatorButton.OPERATOR;
    }

    /** Calculates for basic mode
     *
     */
    private void basicCalculate(){
        if(selectedOperator==Operator.NONE) return;
        if(prevOperand.isEmpty() || currentOperand.isEmpty()) return;
        double result = 0;
        double op1 = toDouble(prevOperand);
        double op2 = toDouble(currentOperand);
        switch(selectedOperator){
            case NONE:
                return;
            case ADD:
                result = op1 + op2;
                break;
            case SUBTRACT:
                    result = op1 - op2;
                    break;
            case DIVIDE:
                result = op1 / op2;
                if(result==Double.POSITIVE_INFINITY || result==Double.NEGATIVE_INFINITY){
                    error = true;
                    prevOperand = "";
                    currentOperand = "";
                }
                break;
            case MULTIPLY:
                result = op1 * op2;
                break;
        }
        prevOperand = new DecimalFormat("#.#######").format(result) + "";
        setDisplay(prevOperand);
        lastPressed = CalculatorButton.NONE;
        if(prevOperand.length()>=MAX_DIGITS || currentOperand.length()>=MAX_DIGITS){
            error=true;
            currentOperand="";
            prevOperand="";
            setDisplay(currentOperand);
            return;
        }
    }

    /** Calculates for formula mode
     *
     */
    private void formulaCaluclate(){
        double result = 0;
        try{
            result = calculator.calculate(currentOperand);
        }
        catch(CalculatorException e){
            error = true;
            currentOperand ="";
        }
        finally {
            currentOperand = new DecimalFormat("#.#######").format(result);
            if(!currentOperand.isEmpty() && currentOperand.charAt(0) == '-'){
                currentOperand = 'n' + currentOperand.substring(1);
            }
            setDisplay(currentOperand);
        }
    }

    /** = button handler
     *
     * @param view
     */
    public void equals(View view) {
        if(mode == Mode.BASIC){
            basicCalculate();
            selectedOperator = Operator.NONE;
            lastPressed = CalculatorButton.EQUALS;
            setSelectedOperator(selectedOperator);
        }
        else if(mode == Mode.FORMULA){
            formulaCaluclate();
            lastPressed = CalculatorButton.EQUALS;
        }

    }

    /** Operand button handler
     *
     * @param view
     */
    public void operand(View view) {
        int id = view.getId();

        if(mode == Mode.BASIC){
            if(lastPressed == CalculatorButton.OPERATOR){
                correctionBuffer = currentOperand;
                currentOperand="";
            }
            else if(lastPressed == CalculatorButton.EQUALS){
                prevOperand = "";
                currentOperand = "";
                correctionBuffer = "";
            }
        }
        else if(mode == Mode.FORMULA){

        }

        if(id == R.id.b0){
            addDigit('0');
        }
        else if(id == R.id.b1){
            addDigit('1');
        }
        else if(id == R.id.b2){
            addDigit('2');
        }
        else if(id == R.id.b3){
            addDigit('3');
        }
        else if(id == R.id.b4){
            addDigit('4');
        }
        else if(id == R.id.b5){
            addDigit('5');
        }
        else if(id == R.id.b6){
            addDigit('6');
        }
        else if(id == R.id.b7){
            addDigit('7');
        }
        else if(id == R.id.b8){
            addDigit('8');
        }
        else if(id == R.id.b9){
            addDigit('9');
        }
        setDisplay(currentOperand);
        lastPressed = CalculatorButton.OPERAND;
    }

    /** Used by the '+/-' button
     *
     * @param view
     */
    public void negate(View view) {
        if(mode == Mode.BASIC){
            if(lastPressed == CalculatorButton.EQUALS){
                prevOperand = "";
                currentOperand = "";
                correctionBuffer = "";
            }
            else if(lastPressed == CalculatorButton.OPERATOR){
                correctionBuffer = currentOperand;
                currentOperand="";
            }
            if(isNegative(currentOperand)){
                currentOperand = currentOperand.substring(1);
            }
            else{
                currentOperand = 'n' + currentOperand;
            }
        }
        else if(mode == Mode.FORMULA){
            currentOperand+='n';
        }

        setDisplay(currentOperand);
        lastPressed = CalculatorButton.OPERAND;
    }


    /** Converts string to a double. Custom parsing is required to look for 'n' to represent negative numbers
     *
     * @param s
     * @return
     */
    private double toDouble(String s){
        if(s.isEmpty()){
            return 0.0;
        }
        else if(s.charAt(0)=='n' && s.length()==1){
            return -0.0;
        }
        else{
            if(s.charAt(0)=='n'){
                return Double.parseDouble(s.substring(1))*-1;
            }
            else{
                return Double.parseDouble(s);
            }
        }
    }

    private boolean isNegative(String s){
        if(s.isEmpty()){
            return false;
        }
        else{
            return s.charAt(0)=='n';
        }
    }

    /** Bracket button event handler
     *
     * @param view
     */
    public void bracket(View view) {
        if(mode==Mode.BASIC) return;

        int id = view.getId();

        if(id == R.id.left_bracket){
            currentOperand+='(';
        }
        else if (id == R.id.right_bracket){
            currentOperand+=')';
        }
        lastPressed = CalculatorButton.OPERAND;
        setDisplay(currentOperand);
    }

    /** MS button handler
     *
     * @param view
     */
    public void store(View view) {
        if(error) return;
        if(mode == Mode.BASIC){
            if(lastPressed==CalculatorButton.EQUALS){
                memory = prevOperand;
            }
            else{
                memory = currentOperand;
            }
        }
        else if(mode == Mode.FORMULA){
            memory = currentOperand;
        }
    }

    /** MR button handler
     *
     * @param view
     */
    public void remember(View view){
        if(error) return;
        if(mode == Mode.BASIC){
            currentOperand = memory;
            if(lastPressed == CalculatorButton.EQUALS){
                prevOperand = "";
                correctionBuffer = "";
            }
        }
        else if(mode == Mode.FORMULA){
            currentOperand+=memory;
        }
        setDisplay(currentOperand);
    }

    private enum Mode{
        BASIC,
        FORMULA;
    }

    private enum Operator{
        NONE,
        ADD,
        SUBTRACT,
        MULTIPLY,
        DIVIDE;
    }

    private enum CalculatorButton{
        NONE,
        OPERATOR,
        OPERAND,
        EQUALS;
    }

    /** Toggles the state between basic and formula mode. NOT the button handler.
     *
     * @param mode
     */
    private void setMode(Mode mode){
        if(mode == Mode.BASIC){
            ToggleButton tb = (ToggleButton) findViewById(R.id.mode);
            tb.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.purple)));
            tb.setTextColor(getColor(R.color.white));

            Button leftBracket = (Button) findViewById(R.id.left_bracket);
            Button rightBracket = (Button) findViewById(R.id.right_bracket);
            leftBracket.setVisibility(View.INVISIBLE);
            rightBracket.setVisibility(View.INVISIBLE);
        }
        else if (mode == Mode.FORMULA) {
            ToggleButton tb = (ToggleButton) findViewById(R.id.mode);
            tb.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.white)));
            tb.setTextColor(getColor(R.color.black));

            Button leftBracket = (Button) findViewById(R.id.left_bracket);
            Button rightBracket = (Button) findViewById(R.id.right_bracket);
            leftBracket.setVisibility(View.VISIBLE);
            rightBracket.setVisibility(View.VISIBLE);
        }


    }

    /** sets the display and converts the internal representation to human readable
     *
     * @param display
     */
    private void setDisplay(String display){
        TextView v = (TextView)findViewById(R.id.display);
        if(mode == Mode.BASIC){
            display = display.isEmpty() ? "0" : display;
            display = display.charAt(0) == '.' ? ('0'+display) : display;
            display = error ? "Error" : display;
            if(display.length() == 1 && display.charAt(0) == 'n'){
                display = "-0";
            }
            else if(display.charAt(0) == 'n'){
                display = '-'+display.substring(1);
            }
        }
        else if(mode == Mode.FORMULA){
            if(error){
                display = "Error";
            }
            else{
                String out = "";
                for(int i = 0; i < display.length(); i++){
                    char c = display.charAt(i);
                    if(c == 'n'){
                        out+='–';
                    }
                    else{
                        out+=c;
                    }
                }
                display = out;
            }

        }
        v.setText(display);
    }

    /** Sets the selected operator on the screen
     *
     * @param o
     */
    private void setSelectedOperator(Operator o){
        Button div = (Button)findViewById(R.id.divide);
        Button add = (Button)findViewById(R.id.add);
        Button mult = (Button)findViewById(R.id.multiply);
        Button sub = (Button)findViewById(R.id.subtract);

        div.setTextColor(getColor(R.color.white));
        add.setTextColor(getColor(R.color.white));
        mult.setTextColor(getColor(R.color.white));
        sub.setTextColor(getColor(R.color.white));

        switch(o){
            case ADD:
                add.setTextColor(getColor(R.color.selectedOperator));
                break;
            case SUBTRACT:
                sub.setTextColor(getColor(R.color.selectedOperator));
                break;
            case MULTIPLY:
                mult.setTextColor(getColor(R.color.selectedOperator));
                break;
            case DIVIDE:
                div.setTextColor(getColor(R.color.selectedOperator));
                break;
        }
    }

    /** Appends a char to the currentOperand variable
     *
     * @param c
     */
    private void addDigit(char c){
        if(currentOperand.length()<=MAX_DIGITS){
            currentOperand+=c;
        }
    }
}