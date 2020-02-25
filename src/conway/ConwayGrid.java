package conway;

import javafx.util.Pair;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class ConwayGrid extends JPanel {
    private int _N;
    private static final int MIN_ELEM = 5, MAX_ELEM = 30;
    private static final int MIN_SEC_C = 1, MAX_SEC_C = 10, DEFAULT_SEC = 2;
    private static final Dimension BTN_SIZE = new Dimension(40, 40);
    private JRadioButton[][] _buttons;
    private boolean[][] _buttonsCopy;
    private GridLayout _layout;
    private JPanel _panel;
    private JSlider sliderSize;
    private JSlider sliderTime;
    private JButton btnStart;
    private Timer timerSecondsCounter;

    public ConwayGrid() {
        this._N = (MAX_ELEM+MIN_ELEM)/2;
        _layout = new GridLayout(_N, _N);
        _panel  = new JPanel(_layout);
        sliderSize = new JSlider(MIN_ELEM, MAX_ELEM, _N);
        sliderTime = new JSlider(MIN_SEC_C,MAX_SEC_C, DEFAULT_SEC);
        timerSecondsCounter = new Timer(DEFAULT_SEC*1000/4, time_updated);
        setupComponents();

        add(_panel);
        gridInit();
        setBackground(Color.RED);
        add(sliderSize);
        add(btnStart);
        add(sliderTime);
    }

    void setupComponents() {
        sliderSize.setMajorTickSpacing(1);
        sliderSize.setPaintTicks(true);
        sliderSize.addChangeListener(this.resize_grid);
        sliderTime.setMajorTickSpacing(1);
        sliderTime.setPaintTicks(true);
        sliderTime.addChangeListener(this.time_change);
        btnStart = new JButton("Begin");
        btnStart.addActionListener(btn_start);
    }

    private void gridInit() {
        _panel.removeAll();
        _layout.setColumns(_N);
        _layout.setRows(_N);
        SwingUtilities.updateComponentTreeUI(_panel);

        _buttons = new JRadioButton[_N][_N];
        for (int i = 0; i < _N; i++) {
            for (int j = 0; j < _N; j++) {
                _buttons[i][j] = new JRadioButton("",false);
                _buttons[i][j].setPreferredSize(BTN_SIZE);
                _buttons[i][j].putClientProperty("id", String.format("%d-%d", i, j));
                _panel.add(_buttons[i][j]);
            }
        }
    }

    private ActionListener btn_start = actionEvent -> {
        btnStart.setText("Pause");
        btnStart.removeActionListener(this.btn_start);
        btnStart.addActionListener(this.btn_stop);
        timerSecondsCounter.start();
    };

    private ActionListener btn_stop = actionEvent -> {
        btnStart.setText("Continue");
        btnStart.removeActionListener(this.btn_stop);
        btnStart.addActionListener(this.btn_start);
        timerSecondsCounter.stop();
    };

    private ChangeListener time_change = new ChangeListener() {
        @Override
        public void stateChanged(ChangeEvent changeEvent) {
            JSlider source = (JSlider) changeEvent.getSource();
            if (!source.getValueIsAdjusting()) {
                int fTime = (source.getValue()*1000/4);
                timerSecondsCounter.setDelay(fTime);
            }
        }
    };

    private ActionListener time_updated = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            _buttonsCopy = new boolean[_N][_N];
            //Yield of new data
            for (int i = 0; i < _N; i++) {
                for (int j = 0; j < _N; j++) {
                    _buttonsCopy[i][j] = getCellState(i, j);
                }
            }

            //Data replication
            for (int i = 0; i < _N; i++) {
                for (int j = 0; j < _N; j++) {
                    _buttons[i][j].setSelected(_buttonsCopy[i][j]);
                }
            }

        }
    };

    private boolean getCellState(int i, int j) {
        boolean state = false;
        int count = 0;
        ArrayList<Pair<Integer, Integer>> possibilities = getAdjacentElem(i, j);

        int x, y;
        for (Pair<Integer, Integer> coords : possibilities) {
            x = coords.getKey();
            y = coords.getValue();
            if (_buttons[x][y].isSelected())
                count++;
        }

        if (count == 3) {
            state = true;
        } else if (count == 2) {
            state = _buttons[i][j].isSelected();
        }

        return state;
    }

    private ArrayList<Pair<Integer, Integer>> getAdjacentElem(int i, int j) {
        ArrayList<Pair<Integer, Integer>> posib = new ArrayList<>();
        if (i > 0){
            posib.add(new Pair<>(i-1, j));
            if (j > 0)
                posib.add(new Pair<>(i-1, j-1));
            if (j < _N-1)
                posib.add(new Pair<>(i-1, j+1));
        }
        if (i < _N-1) {
            posib.add(new Pair<>(i+1, j));
            if (j > 0)
                posib.add(new Pair<>(i+1, j-1));
            if (j < _N-1)
                posib.add(new Pair<>(i+1, j+1));
        }
        if (j > 0){
            posib.add(new Pair<>(i, j-1));
        }
        if (j < _N-1) {
            posib.add(new Pair<>(i, j+1));
        }
        return posib;
    }


    private ChangeListener resize_grid = new ChangeListener() {
        @Override
        public void stateChanged(ChangeEvent changeEvent) {
            JSlider source = (JSlider) changeEvent.getSource();
            if (!source.getValueIsAdjusting()) {
                _N = source.getValue();
                gridInit();
            }
        }
    };

}
