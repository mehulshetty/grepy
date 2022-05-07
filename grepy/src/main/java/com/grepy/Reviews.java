package com.grepy;

import java.util.Random;

public class Reviews {

    public static String getReview() {
        Random rand = new Random();

        // As we discover more universes we will keep adding new reviews
        String[] reviews = {"I have watched every single Marvel film with my Dad. I’ll often fly cross country to go see them in person with him. That came up in conversation with my boss Sam Raimi and I found myself gifted two tickets to the World Premiere of his new film. I got to take my dad down the red carpet and introduced him to Sam. It was a perfect night. We smiled like crazy watching this in a packed theater (we sat right behind Danny Elfman). Oh and the movie was a blast. Brimming with energy and a hell of a lot of honest to god scares. It’s an earnest, colorful ride. You don’t hire Sam for this gig without expecting a hell of a lot of pulp (and ummmm musical notes). Oh and those Cameos…",
                            "The morbius and superman cameo stole the show the crowd went wild",
                            "I really loved this documentary of Doctor Strange. Didn't know all the struggles he went through to bring peace to our universe",
                            "I liked the movie. Was just not too happy on how they ended things with a specific character. Besides that it was an enjoyable ride. Scary at times. They could've done so much more with the multiverse concept.",
                            "Das passiert also, wenn der Evil-Dead-Typ mit dem Rick-and-Morty-Schreiber einen neuen Film aus den Bruchstücken eines ursprünglichen Films zusammenschustert. Sehr Sam Raimi. Sehr Michael Waldron. Sehr anders für Marvel - und trotzdem sehr typisch. Ich hatte sehr viel Spaß damit!",
                            "The entire theatre gave a standing ovation when Tom Cruise stepped out of that portal and said 'I guess the real Superior Iron Man was the friends we made along the way'",
                            "More like Doctor Strange in the Multiverse of Midness"};
        return reviews[rand.nextInt(reviews.length - 1)];
    }
    
}
