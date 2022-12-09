package com.johnpickup.aoc2021;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day24Alu {
    List<String> lines;

    public static void main(String[] args) {
        Day24Alu day24 = new Day24Alu("/Users/john/Development/AdventOfCode/resources/Day24Test.txt");
        day24.solve("21111111111111");
    }

    private void solve(String input) {
        List<Instruction> instructions = lines.stream().map(Instruction::new).collect(Collectors.toList());

        Alu alu = new Alu(input);
        alu.execute(instructions);
        System.out.println(alu);
        System.out.println("Z=" + alu.zS);
        System.out.println("W=" + alu.wS);
        System.out.println("X=" + alu.xS);
        System.out.println("Y=" + alu.yS);
    }

    public Day24Alu(String filename) {
        try (Stream<String> stream = Files.lines(Paths.get(filename))) {
            lines = stream.collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class Instruction {
        String operator;
        String arg1;
        String arg2;

        static int idx = 0;

        public Instruction(String s) {
            String[] strings = s.split(" ");
            this.operator = strings[0];
            this.arg1 = strings[1];
            this.arg2 = strings.length==3?strings[2]:null;
        }

        public void execute(Alu alu) {
            switch (operator) {
                case "inp":
                    int input = alu.getInput();
                    alu.storeExpr("I"+(alu.inputIndex), arg1);
                    alu.store(input, arg1);
                    break;
                case "add":
                    if (alu.valueExpr(arg1).equals("0")) alu.storeExpr(alu.valueExpr(arg2), arg1);
                    else alu.storeExpr(alu.valueExpr(arg2).equals("0")? alu.valueExpr(arg1) : alu.valueExpr(arg1) + "+" + alu.valueExpr(arg2), arg1);
                    alu.store(alu.value(arg1) + alu.value(arg2), arg1);
                    break;
                case "mul":
                    if (alu.valueExpr(arg1).equals("0")) alu.storeExpr("0", arg1);
                    else if (alu.valueExpr(arg2).equals("0")) alu.storeExpr("0", arg1);
                    else if (alu.valueExpr(arg2).equals("1")) alu.storeExpr(alu.valueExpr(arg1), arg1);  // do nothing
                    else alu.storeExpr("("+alu.valueExpr(arg1)+")*(" + alu.valueExpr(arg2)+")", arg1);
                    alu.store(alu.value(arg1) * alu.value(arg2), arg1);
                    break;
                case "div":
                    if (alu.valueExpr(arg1).equals("0")) alu.storeExpr("0", arg1);
                    else if (alu.valueExpr(arg2).equals("1")) alu.storeExpr(alu.valueExpr(arg1), arg1);  // do nothing
                    else alu.storeExpr("("+alu.valueExpr(arg1)+")/(" + alu.valueExpr(arg2)+")", arg1);
                    alu.store(alu.value(arg1) / alu.value(arg2), arg1);
                    break;
                case "mod":
                    if (alu.valueExpr(arg1).equals("0")) alu.storeExpr("0", arg1);
                    else alu.storeExpr("("+alu.valueExpr(arg1)+")%(" + alu.valueExpr(arg2)+")", arg1);
                    alu.store(alu.value(arg1) % alu.value(arg2), arg1);
                    break;
                case "eql":
                    alu.storeExpr("("+alu.valueExpr(arg1)+")==(" + alu.valueExpr(arg2)+")", arg1);
                    alu.store(alu.value(arg1) == alu.value(arg2)?1:0, arg1);
                    break;
                default:
                    throw new RuntimeException("Unknown instruction " + this);
            }
            System.out.printf("%03d: %-10s  [%s]\n", ++idx, this, alu.toString());
        }

        @Override
        public String toString() {
            return operator + ' ' +
                    arg1 + ' ' + (arg2==null?"":arg2);
        }
    }

    static class Alu {
        int w=0, x=0, y=0, z=0;
        String wS="0", xS="0", yS="0", zS="0";
        int inputIndex = 0;

        public Alu(String input) {
            this.input = input;
        }

        String input;

        public int getInput() {
            return input.charAt(inputIndex++) - '0';
        }

        public void store(int value, String register) {
            switch (register) {
                case "w": w = value; break;
                case "x": x = value; break;
                case "y": y = value; break;
                case "z": z = value; break;
                default: throw new RuntimeException("Unknown register " + register);
            }
        }

        public void storeExpr(String value, String register) {
            switch (register) {
                case "w": wS = value; break;
                case "x": xS = value; break;
                case "y": yS = value; break;
                case "z": zS = value; break;
                default: throw new RuntimeException("Unknown register " + register);
            }
        }

        public int value(String registerOrLiteral) {
            switch (registerOrLiteral) {
                case "w": return w;
                case "x": return x;
                case "y": return y;
                case "z": return z;
                default: return Integer.parseInt(registerOrLiteral);
            }
        }

        public String valueExpr(String registerOrLiteral) {
            switch (registerOrLiteral) {
                case "w": return wS;
                case "x": return xS;
                case "y": return yS;
                case "z": return zS;
                default: return registerOrLiteral;
            }
        }


        @Override
        public String toString() {
            return "Alu{" +
                    "w=" + w +
                    ", x=" + x +
                    ", y=" + y +
                    ", z=" + z +
                    '}';
        }

        public void execute(List<Instruction> instructions) {
            for (Instruction instruction : instructions) {
                instruction.execute(this);
            }
        }
    }
}
