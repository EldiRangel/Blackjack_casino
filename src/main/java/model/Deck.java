package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Deck {
    private List<Card> cards = new ArrayList<>();

    public Deck() {
        String[] suits = {"Hearts", "Diamonds", "Clubs", "Spades"};
        String[] ranks = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A"};
        for (String s : suits) {
            for (String r : ranks) {
                int val = r.equals("A") ? 11 : (r.matches("\\d+") ? Integer.parseInt(r) : 10);
                cards.add(new Card(s, r, val));
            }
        }
        Collections.shuffle(cards);
    }

    public Card draw() { return cards.isEmpty() ? null : cards.remove(0); }
}