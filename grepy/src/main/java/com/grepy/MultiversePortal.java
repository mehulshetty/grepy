package com.grepy;

import java.util.Random;

public class MultiversePortal {

    // Enter the multiverse to find reviews on Doctor Strange in the Multiverse of Madness from different versions of me
    public static void enter() {
        Random rand = new Random();

        System.out.println("\n\nPLEASE HOLD ON TIGHT AND TRY NOT TO PUKE...");

        Boolean earthQuestionMark = rand.nextBoolean();

        if(earthQuestionMark) {
            System.out.println("You have arrived on Earth in the Universe " + rand.nextInt(1000));
            System.out.println("\nHere's Mehul's Review for Doctor Strange in the Multiverse of Madness: ");
            System.out.println(Reviews.getReview());
        }
        else {
            System.out.println("You have arrived in the Universe " + rand.nextInt());
            System.out.println("Earth has either been destroyed or was never in this universe. " + 
                                "In either case, RUN before they find you.");
        }


    }
}
