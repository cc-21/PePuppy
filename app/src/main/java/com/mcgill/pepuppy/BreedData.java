package com.mcgill.pepuppy;

import java.util.Arrays;
import java.util.List;

public class BreedData {
    private static final String[] breedInfo = {"Blue Lacy","Queensland Heeler","Rhod Ridgeback","Retriever",
            "Sharpei","Black Mouth Cur","Catahoula","Staffordshire","Affenpinscher","Afghan Hound",
            "Airedale Terrier","Akita","Australian Kelpie","Alaskan Malamute","English Bulldog","American Bulldog",
            "American English Coonhound","American Eskimo Dog","American Eskimo Dog",
            "American Eskimo Dog (Toy)","American Foxhound","American Hairless Terrier","American Staffordshire Terrier",
            "American Water Spaniel","Anatolian Shepherd Dog","Australian Cattle Dog","Australian Shepherd",
            "Australian Terrier","Basenji","Basset Hound","Beagle","Bearded Collie","Beauceron","Bedlington Terrier",
            "Belgian Malinois","Belgian Sheepdog","Belgian Tervuren","Bergamasco","Berger Picard","Bernese Mountain Dog",
            "Bichon Fris_","Black and Tan Coonhound","Black Russian Terrier","Bloodhound","Bluetick Coonhound","Boerboel",
            "Border Collie","Border Terrier","Borzoi","Boston Terrier","Bouvier des Flandres","Boxer","Boykin Spaniel",
            "Briard","Brittany","Brussels Griffon","Bull Terrier","Bull Terrier","Bulldog","Bullmastiff","Cairn Terrier",
            "Canaan Dog","Cane Corso","Cardigan Welsh Corgi","Cavalier King Charles Spaniel","Cesky Terrier","Chesapeake Bay Retriever",
            "Chihuahua","Chinese Crested Dog","Chinese Shar Pei","Chinook","Chow Chow","Cirneco dell'Etna","Clumber Spaniel","Cocker Spaniel",
            "Collie","Coton de Tulear","Curly-Coated Retriever","Dachshund","Dalmatian","Dandie Dinmont Terrier","Doberman Pinsch",
            "Doberman Pinscher","Dogue De Bordeaux","English Cocker Spaniel","English Foxhound","English Setter","English Springer Spaniel",
            "English Toy Spaniel","Entlebucher Mountain Dog","Field Spaniel","Finnish Lapphund","Finnish Spitz","Flat-Coated Retriever",
            "French Bulldog","German Pinscher","German Shepherd","German Shorthaired Pointer","German Wirehaired Pointer","Giant Schnauzer",
            "Glen of Imaal Terrier","Golden Retriever","Gordon Setter","Great Dane","Great Pyrenees","Greater Swiss Mountain Dog","Greyhound",
            "Harrier","Havanese","Ibizan Hound","Icelandic Sheepdog","Irish Red and White Setter","Irish Setter","Irish Terrier",
            "Irish Water Spaniel","Irish Wolfhound","Italian Greyhound","Japanese Chin","Keeshond","Kerry Blue Terrier","Komondor",
            "Kuvasz","Labrador Retriever","Lagotto Romagnolo","Lakeland Terrier","Leonberger","Lhasa Apso","L_wchen","Maltese",
            "Manchester Terrier","Mastiff","Miniature American Shepherd","Miniature Bull Terrier","Miniature Pinscher","Miniature Schnauzer",
            "Neapolitan Mastiff","Newfoundland","Norfolk Terrier","Norwegian Buhund","Norwegian Elkhound","Norwegian Lundehund","Norwich Terrier",
            "Nova Scotia Duck Tolling Retriever","Old English Sheepdog","Otterhound","Papillon","Parson Russell Terrier","Pekingese",
            "Pembroke Welsh Corgi","Petit Basset Griffon Vend_en","Pharaoh Hound","Plott","Pointer","Polish Lowland Sheepdog","Pomeranian",
            "Standard Poodle","Miniature Poodle","Toy Poodle","Portuguese Podengo Pequeno","Portuguese Water Dog","Pug","Puli","Pyrenean Shepherd",
            "Rat Terrier","Redbone Coonhound","Rhodesian Ridgeback","Rottweiler","Russell Terrier","St. Bernard","Saluki","Samoyed","Schipperke",
            "Scottish Deerhound","Scottish Terrier","Sealyham Terrier","Shetland Sheepdog","Shiba Inu","Shih Tzu","Siberian Husky","Silky Terrier",
            "Skye Terrier","Sloughi","Smooth Fox Terrier","Soft-Coated Wheaten Terrier","Spanish Water Dog","Spinone Italiano","Staffordshire Bull Terrier",
            "Standard Schnauzer","Sussex Spaniel","Swedish Vallhund","Tibetan Mastiff","Tibetan Spaniel","Tibetan Terrier","Toy Fox Terrier",
            "Treeing Walker Coonhound","Vizsla","Weimaraner","Welsh Springer Spaniel","Welsh Terrier","West Highland White Terrier","Whippet",
            "Wire Fox Terrier","Wirehaired Pointing Griffon","Wirehaired Vizsla","Xoloitzcuintli","Yorkshire Terrier"};

    private static List<String> breedData = Arrays.asList(breedInfo);

    public static boolean contains(String pName)
    {
        return breedData.stream()
                .anyMatch(breed -> breed.trim().toLowerCase().equals(pName.trim().toLowerCase()));
    }
}
