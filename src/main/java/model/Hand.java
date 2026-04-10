package model;

import java.util.ArrayList;
import java.util.List;

public class Hand {
    private List<Card> cards = new ArrayList<>();

    public void add(Card card) {
        if (card != null) {
            cards.add(card);
        }
    }

    public int getTotal() {
        int total = 0;
        int aces = 0;
        for (Card c : cards) {
            total += c.getValue();
            if (c.getValue() == 11) aces++;
        }
        // Si se pasa de 21, el As baja de 11 a 1
        while (total > 21 && aces > 0) {
            total -= 10;
            aces--;
        }
        return total;
    }

    @Override
    public String toString() {
        return cards.toString();
    }
}