package org.example.calculator;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Calculator {
    private String function;
    private List<Double> values;
    public Calculator(String operation){
        this.function = findFunc(operation);
        this.values = findValues(operation);
        System.out.println("CALCULATING: " + function);
        System.out.println("VALUES" + values);
    }

    public String calculate() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, ClassNotFoundException {
        if(this.function.equals("qck")){
            quickSort(this.values, 0, this.values.size());
            return this.values.toString();
        }
        Double result = null;
        Class<?> mathClass = Class.forName("java.lang.Math");
        Class[] argTypes = new Class[values.size()];
        for (int i = 0; i < values.size(); i++) {
            argTypes[i] = double.class;
        }
        Method function = mathClass.getDeclaredMethod(this.function, argTypes);
        result = (Double) function.invoke(null, values.toArray());
        System.out.println(result);
        return String.valueOf(result);
    }

    public void quickSort(List<Double> arr, int start, int end){
        if(start < end){
            int pivotPos = partition(arr, start, end);
            quickSort(arr, pivotPos + 1, end);
            quickSort(arr, start, pivotPos - 1);
        }
    }

    private int partition(List<Double> arr, int start, int end){
        double pivot = arr.get(arr.size() - 1);
        int i;
        for (i = start; i < end; i++) {
            if (arr.get(i) > pivot){
                arr.add(i, pivot);
                arr.remove(arr.size() - 1);
                break;
            }
        }
        return i;
    }

    private String findFunc(String operation){
        StringBuilder func = new StringBuilder();
        for (int i = 0; i < operation.length(); i++){
            if (operation.charAt(i) == '('){
                break;
            }
            func.append(operation.charAt(i));
        }
        return func.toString();
    }

    private List<Double> findValues(String operation){
        int start = operation.indexOf('(');
        String numbers = operation.substring(start + 1, operation.length() - 1);
        String[] numberList = numbers.split(",");
        List<Double> doubleList = new ArrayList<>();
        for (String num : numberList) {
            doubleList.add(Double.valueOf(num));
        }
        return doubleList;
    }
}
