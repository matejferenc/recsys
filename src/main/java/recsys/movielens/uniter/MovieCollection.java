package recsys.movielens.uniter;

import java.text.Normalizer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.mahout.common.Pair;

import recsys.movielens.model.shared.Movie;

public class MovieCollection<T> {

	private Map<MovieId, Movie<T>> movies;
	
	private static final String[] endArticles = {", the", ", les", ", il", ", la", ", le", ", an", ", a", ", l'", ", i", ", das", ", el", ", der", ", o", ", det", ", die"};

	private static final String[] startArticles = {"the ", "les ", "il ", "la ", "le ", "an ", "a ", "l'", "i ", "das ", "el ", "der ", "o ", "det ", "die "};
	
	private static final Map<Pair<String, Integer>, Pair<String, Integer>> movielensToIMDbDictionary;
	
	private static final Map<Pair<String, Integer>, Pair<String, Integer>> movielensToIMDbDictionary2;
    
	static {
		movielensToIMDbDictionary = new HashMap<Pair<String, Integer>, Pair<String, Integer>>();
		movielensToIMDbDictionary2 = new HashMap<Pair<String, Integer>, Pair<String, Integer>>();
		movielensToIMDbDictionary.put(pair("Gumby: The Movie", 1995), pair("Gumby 1", 1995));
		movielensToIMDbDictionary.put(pair("Shanghai Triad (Yao a yao yao dao waipo qiao)", 1995), pair("Yao a yao, yao dao wai po qiao", 1995));
		movielensToIMDbDictionary.put(pair("Happiness Is in the Field", 1995), pair("Le bonheur est dans le pré", 1995));
		movielensToIMDbDictionary.put(pair("Interview with the Vampire", 1994), pair("Interview with the Vampire: The Vampire Chronicles", 1994));
		movielensToIMDbDictionary.put(pair("New York Cop", 1996), pair("New York Undercover Cop", 1993));
		movielensToIMDbDictionary.put(pair("Professional, The (a.k.a. Leon: The Professional)", 1994), pair("Léon", 1994));
		movielensToIMDbDictionary.put(pair("Pushing Hands", 1992), pair("Tui Shou", 1992));
		movielensToIMDbDictionary.put(pair("Three Colors: White", 1994), pair("Trois couleurs: Blanc", 1994));
		movielensToIMDbDictionary.put(pair("Tales From the Crypt Presents: Demon Knight", 1995), pair("Tales From the Crypt: Demon Knight", 1995));
		movielensToIMDbDictionary.put(pair("Wonderful, Horrible Life of Leni Riefenstahl, The (Die Macht der Bilder)", 1993), pair("Die Macht der Bilder: Leni Riefenstahl", 1993));
		movielensToIMDbDictionary.put(pair("Two Crimes", 1995), pair("Dos crímenes", 1995));
		movielensToIMDbDictionary.put(pair("In the Realm of the Senses (Ai no corrida)", 1976), pair("Ai no korîda", 1976));
		movielensToIMDbDictionary.put(pair("Harlem", 1993), pair("Sugar Hill", 1993));
		movielensToIMDbDictionary.put(pair("Window to Paris", 1994), pair("Okno v Parizh", 1993));
		movielensToIMDbDictionary.put(pair("Three Colors: Blue", 1993), pair("Trois couleurs: Bleu", 1993));
		movielensToIMDbDictionary.put(pair("Three Colors: Red", 1994), pair("Trois couleurs: Rouge", 1994));
		movielensToIMDbDictionary.put(pair("Mary Shelley's Frankenstein", 1994), pair("Frankenstein", 1994));
		movielensToIMDbDictionary.put(pair("Couch in New York", 1996), pair("Un divan à New York", 1996));
		movielensToIMDbDictionary.put(pair("And God Created Woman (Et Dieu&#8230;Créa la Femme)", 1956), pair("Et Dieu... créa la femme", 1956));
		movielensToIMDbDictionary.put(pair("Everything You Always Wanted to Know About Sex", 1972), pair("Everything You Always Wanted to Know About Sex * But Were Afraid to Ask", 1972));
		movielensToIMDbDictionary.put(pair("Pokémon the Movie 2000", 2000), pair("Gekijô-ban poketto monsutâ: Maboroshi no pokemon: Rugia bakutan", 1999));
		movielensToIMDbDictionary.put(pair("F/X 2", 1992), pair("F/X2", 1991));
		movielensToIMDbDictionary.put(pair("Daughter of Dr. Jeckyll", 1957), pair("Daughter of Dr. Jekyll", 1957));
		movielensToIMDbDictionary.put(pair("Switchblade Sisters", 1975), pair("The Jezebels", 1975));
		movielensToIMDbDictionary.put(pair("Ashes of Time", 1994), pair("Dung che sai duk", 1994));
		movielensToIMDbDictionary.put(pair("Spirits of the Dead (Tre Passi nel Delirio)", 1968), pair("Histoires extraordinaires", 1968));
		movielensToIMDbDictionary.put(pair("An Unforgettable Summer", 1994), pair("Un été inoubliable", 1994));
		movielensToIMDbDictionary.put(pair("Hungarian Fairy Tale, A", 1987), pair("Hol volt, hol nem volt", 1987));
		movielensToIMDbDictionary.put(pair("Time Regained (Le Temps Retrouvé)", 1999), pair("Le temps retrouvé, d'après l'oeuvre de Marcel Proust", 1999));
		movielensToIMDbDictionary.put(pair("Battleship Potemkin, The (Bronenosets Potyomkin)", 1925), pair("Bronenosets Potemkin", 1925));
		movielensToIMDbDictionary.put(pair("Live Virgin", 1999), pair("American Virgin", 1999));
		movielensToIMDbDictionary.put(pair("For a Few Dollars More", 1965), pair("Per qualche dollaro in più", 1965));
		movielensToIMDbDictionary.put(pair("Retro Puppetmaster", 1999), pair("Retro Puppet Master", 1999));
		movielensToIMDbDictionary.put(pair("Woman of Paris, A", 1923), pair("A Woman of Paris: A Drama of Fate", 1923));
		movielensToIMDbDictionary.put(pair("Slipper and the Rose, The", 1976), pair("The Slipper and the Rose: The Story of Cinderella", 1976));
		movielensToIMDbDictionary.put(pair("Black Tights (Les Collants Noirs)", 1960), pair("1-2-3-4 ou Les collants noirs", 1961));
		movielensToIMDbDictionary.put(pair("Two Moon Juction", 1988), pair("Two Moon Junction", 1988));
		movielensToIMDbDictionary.put(pair("Law, The (Le Legge)", 1958), pair("La legge", 1959));
		movielensToIMDbDictionary.put(pair("Jonah Who Will Be 25 in the Year 2000", 1976), pair("Jonas qui aura 25 ans en l'an 2000", 1976));
		movielensToIMDbDictionary.put(pair("Bear", 1988), pair("L'ours", 1988));
		movielensToIMDbDictionary.put(pair("Captain Horatio Hornblower", 1951), pair("Captain Horatio Hornblower R.N.", 1951));
		movielensToIMDbDictionary.put(pair("Eat Drink Man Woman", 1994), pair("Yin shi nan nu", 1994));
		movielensToIMDbDictionary.put(pair("Star Wars: Episode IV - A New Hope", 1977), pair("Star Wars", 1977));
		movielensToIMDbDictionary.put(pair("Mutters Courage", 1995), pair("My Mother's Courage", 1995));
		movielensToIMDbDictionary.put(pair("Purple Noon", 1960), pair("Plein soleil", 1960));
		movielensToIMDbDictionary.put(pair("All Things Fair", 1996), pair("Lust och fägring stor", 1995));
		movielensToIMDbDictionary.put(pair("Alphaville", 1965), pair("Alphaville, une étrange aventure de Lemmy Caution", 1965));
		movielensToIMDbDictionary.put(pair("Goya in Bordeaux (Goya en Bodeos)", 1999), pair("Goya en Burdeos", 1999));
		movielensToIMDbDictionary.put(pair("Hard-Boiled (Lashou shentan)", 1992), pair("Lat sau san taam", 1992));
		movielensToIMDbDictionary.put(pair("Beloved/Friend (Amigo/Amado)", 1999), pair("Amic/Amat", 1999));
		movielensToIMDbDictionary.put(pair("Gendernauts", 1999), pair("Gendernauts - Eine Reise durch die Geschlechter", 1999));
		movielensToIMDbDictionary.put(pair("Uninvited Guest, An", 2000), pair("An Invited Guest", 1999));
		movielensToIMDbDictionary.put(pair("Couch in New York, A", 1996), pair("Un divan à New York", 1996));
		movielensToIMDbDictionary.put(pair("I'll Never Forget What's 'is Name", 1967), pair("I'll Never Forget What's'isname", 1967));
		movielensToIMDbDictionary.put(pair("McCullochs, The", 1975), pair("The Wild McCullochs", 1975));
		movielensToIMDbDictionary.put(pair("Cry in the Dark, A", 1988), pair("Evil Angels", 1988));
		movielensToIMDbDictionary.put(pair("Every Other Weekend", 1990), pair("Un week-end sur deux", 1990));
		movielensToIMDbDictionary.put(pair("Cyclo", 1995), pair("Xich lo", 1995));
		movielensToIMDbDictionary.put(pair("Day the Sun Turned Cold, The (Tianguo niezi)", 1994), pair("Tian guo ni zi", 1994));
		movielensToIMDbDictionary.put(pair("Story of Xinghua, The", 1993), pair("Xinghua san yue tian", 1994));
		movielensToIMDbDictionary.put(pair("Eyes Without a Face", 1959), pair("Les yeux sans visage", 1960));
		movielensToIMDbDictionary.put(pair("Magic Hunter", 1994), pair("Büvös vadász", 1994));
		movielensToIMDbDictionary.put(pair("Farewell My Concubine", 1993), pair("Ba wang bie ji", 1993));
		movielensToIMDbDictionary.put(pair("Twelfth Night", 1996), pair("Twelfth Night or What You Will", 1996));
		movielensToIMDbDictionary.put(pair("Name of the Rose, The", 1986), pair("Der Name der Rose", 1986));
		movielensToIMDbDictionary.put(pair("Karate Kid III, The", 1989), pair("The Karate Kid, Part III", 1989));
		movielensToIMDbDictionary.put(pair("King Kong vs. Godzilla (Kingukongu tai Gojira)", 1962), pair("Kingu Kongu tai Gojira", 1962));
		movielensToIMDbDictionary.put(pair("Alarmist, The", 1997), pair("Life During Wartime", 1997));
		movielensToIMDbDictionary.put(pair("Monument Ave.", 1998), pair("Snitch", 2008));
		movielensToIMDbDictionary.put(pair("Master Ninja I", 1984), pair("The Master", 1984));
		movielensToIMDbDictionary.put(pair("Juno and Paycock", 1930), pair("Juno and the Paycock", 1929));
		movielensToIMDbDictionary.put(pair("Merry War, A", 1997), pair("Keep the Aspidistra Flying", 1997));
		movielensToIMDbDictionary.put(pair("NeverEnding Story, The", 1984), pair("Die unendliche Geschichte", 1984));
		movielensToIMDbDictionary.put(pair("Henry: Portrait of a Serial Killer, Part 2", 1996), pair("Henry II: Portrait of a Serial Killer", 1996));
		movielensToIMDbDictionary.put(pair("Chambermaid on the Titanic, The", 1998), pair("La femme de chambre du Titanic", 1997));
		movielensToIMDbDictionary.put(pair("Saltmen of Tibet, The", 1997), pair("Die Salzmänner von Tibet", 1997));
		movielensToIMDbDictionary.put(pair("Ever After: A Cinderella Story", 1998), pair("EverAfter", 1998));
		movielensToIMDbDictionary.put(pair("Fanny and Alexander", 1982), pair("Fanny och Alexander", 1982));
		movielensToIMDbDictionary.put(pair("Halloween 5: The Revenge of Michael Myers", 1989), pair("Halloween 5", 1989));//try splitting on ':'
		movielensToIMDbDictionary.put(pair("Friday the 13th Part VI: Jason Lives", 1986), pair("Jason Lives: Friday the 13th Part VI", 1986));
		movielensToIMDbDictionary.put(pair("Friday the 13th Part V: A New Beginning", 1985), pair("Friday the 13th: A New Beginning", 1985));
		movielensToIMDbDictionary.put(pair("Friday the 13th Part 3: 3D", 1982), pair("Friday the 13th Part III", 1982));
		movielensToIMDbDictionary.put(pair("Nightmare on Elm Street 5: The Dream Child, A", 1989), pair("A Nightmare on Elm Street: The Dream Child", 1989));
		movielensToIMDbDictionary.put(pair("Buffalo 66", 1998), pair("Buffalo '66", 1998));
		movielensToIMDbDictionary.put(pair("Voyage to the Beginning of the World", 1997), pair("Viagem ao Princípio do Mundo", 1997));
		movielensToIMDbDictionary.put(pair("Beyond Silence", 1996), pair("Jenseits der Stille", 1996));
		movielensToIMDbDictionary.put(pair("Friend of the Deceased, A", 1997), pair("Priyatel pokoynika", 1997));
		movielensToIMDbDictionary.put(pair("Truce, The", 1996), pair("La tregua", 1997));
		movielensToIMDbDictionary.put(pair("Junk Mail", 1997), pair("Budbringeren", 1997));
		movielensToIMDbDictionary.put(pair("Taste of Cherry", 1997), pair("Ta'm e guilass", 1997));
		movielensToIMDbDictionary.put(pair("Mr. Nice Guy", 1997), pair("Yat goh ho yan", 1997));
		movielensToIMDbDictionary.put(pair("Live Flesh", 1997), pair("Carne trémula", 1997));
		movielensToIMDbDictionary.put(pair("Player's Club, The", 1998), pair("The Players Club", 1998));
		movielensToIMDbDictionary.put(pair("U.S. Marshalls", 1998), pair("U.S. Marshals", 1998));
		movielensToIMDbDictionary.put(pair("Mat' i syn", 1997), pair("Mat i syn", 1997));//try removing apostrophes?
		movielensToIMDbDictionary.put(pair("Four Days in September", 1997), pair("O Que É Isso, Companheiro?", 1997));
		movielensToIMDbDictionary.put(pair("3 Ninjas: High Noon On Mega Mountain", 1998), pair("3 Ninjas: High Noon at Mega Mountain", 1998));
		movielensToIMDbDictionary.put(pair("Full Speed", 1996), pair("À toute vitesse", 1996));
		movielensToIMDbDictionary.put(pair("Hurricane Streets", 1998), pair("Hurricane", 1997));
		movielensToIMDbDictionary.put(pair("A Chef in Love", 1996), pair("Shekvarebuli kulinaris ataserti retsepti", 1996));
		movielensToIMDbDictionary.put(pair("Prisoner of the Mountains (Kavkazsky Plennik)", 1996), pair("Kavkazskiy plennik", 1996));
		movielensToIMDbDictionary.put(pair("Kolya", 1996), pair("Kolja", 1996));
		movielensToIMDbDictionary.put(pair("Raise the Red Lantern", 1991), pair("Da hong deng long gao gao gua", 1991));
		movielensToIMDbDictionary.put(pair("Once Upon a Time in the West", 1969), pair("C'era una volta il West", 1968));
		movielensToIMDbDictionary.put(pair("Good, The Bad and The Ugly, The", 1966), pair("Il buono, il brutto, il cattivo.", 1966));
		movielensToIMDbDictionary.put(pair("Tie Me Up! Tie Me Down!", 1990), pair("Átame!", 1989));
		movielensToIMDbDictionary.put(pair("Cinema Paradiso", 1988), pair("Nuovo Cinema Paradiso", 1988));
		movielensToIMDbDictionary.put(pair("Two or Three Things I Know About Her", 1966), pair("2 ou 3 choses que je sais d'elle", 1967));
		movielensToIMDbDictionary.put(pair("Line King: Al Hirschfeld, The", 1996), pair("The Line King: The Al Hirschfeld Story", 1996));
		movielensToIMDbDictionary.put(pair("Three Lives and Only One Death", 1996), pair("Trois vies et une seule mort", 1996));
		movielensToIMDbDictionary.put(pair("Tashunga", 1995), pair("North Star", 1996));
		movielensToIMDbDictionary.put(pair("Children of the Corn IV: The Gathering", 1996), pair("Children of the Corn: The Gathering", 1996));
		movielensToIMDbDictionary.put(pair("Victor/Victoria", 1982), pair("Victor Victoria", 1982));//try fixing slash '/'
		movielensToIMDbDictionary.put(pair("Sexual Life of the Belgians, The", 1994), pair("La vie sexuelle des Belges 1950-1978", 1994));
		movielensToIMDbDictionary.put(pair("Bitter Sugar (Azucar Amargo)", 1996), pair("Azúcar amarga", 1996));
		movielensToIMDbDictionary.put(pair("Associate, The (L'Associe)", 1982), pair("L'associé", 1979));
		movielensToIMDbDictionary.put(pair("In the Line of Duty 2", 1987), pair("Wong ga jin si", 1986));
		movielensToIMDbDictionary.put(pair("Wild Reeds", 1994), pair("Les roseaux sauvages", 1994));
		movielensToIMDbDictionary.put(pair("Aiqing wansui", 1994), pair("Ai qing wan sui", 1994));
		movielensToIMDbDictionary.put(pair("Supercop", 1992), pair("Ging chat goo si 3: Chiu kup ging chat", 1992));
		movielensToIMDbDictionary.put(pair("City of Lost Children, The", 1995), pair("La cité des enfants perdus", 1995));
		movielensToIMDbDictionary.put(pair("National Lampoon's Senior Trip", 1995), pair("Senior Trip", 1995));
		movielensToIMDbDictionary.put(pair("To Live (Huozhe)", 1994), pair("Huo zhe", 1994));
		movielensToIMDbDictionary.put(pair("Dangerous Game", 1993), pair("Snake Eyes", 1993));
		movielensToIMDbDictionary.put(pair("My Favorite Season", 1993), pair("Ma saison préférée", 1993));
		movielensToIMDbDictionary.put(pair("Associate, The (L'Associe", 1982), pair("L'associé", 1979));
		movielensToIMDbDictionary.put(pair("Duoluo tianshi", 1995), pair("Duo luo tian shi", 1995));
		//this translation is needed only for keywords
		movielensToIMDbDictionary2.put(pair("Duoluo tianshi", 1995), pair("Do lok tin si", 1995));
		movielensToIMDbDictionary.put(pair("Mafia!", 1998), pair("Jane Austen's Mafia!", 1998));
		//this translation is needed only for keywords.. FIX!!!
		movielensToIMDbDictionary2.put(pair("Navigator: A Mediaeval Odyssey, The", 1988), pair("The Navigator: A Medieval Odyssey", 1988));
		movielensToIMDbDictionary.put(pair("Jackie Chan's First Strike", 1996), pair("Ging chaat goo si 4: Ji gaan daan yam mo", 1996));
		//this translation is needed only for keywords.. FIX!!!
		movielensToIMDbDictionary2.put(pair("Jackie Chan's First Strike", 1996), pair("Ging chat goo si 4: Ji gaan daan yam mo", 1996));
		movielensToIMDbDictionary.put(pair("Harmonists, The", 1997), pair("Comedian Harmonists", 1997));
		movielensToIMDbDictionary.put(pair("Children of the Corn III", 1994), pair("Children of the Corn III: Urban Harvest", 1995));
		movielensToIMDbDictionary.put(pair("Twin Dragons (Shuang long hui)", 1992), pair("Seong lung wui", 1992));
		movielensToIMDbDictionary.put(pair("Howling II: Your Sister Is a Werewolf", 1985), pair("Howling II: Stirba - Werewolf Bitch", 1985));
		movielensToIMDbDictionary.put(pair("Eternity and a Day (Mia eoniotita ke mia mera )", 1998), pair("Mia aioniotita kai mia mera", 1998));
		movielensToIMDbDictionary.put(pair("Twice Upon a Yesterday", 1998), pair("The Man with Rain in His Shoes", 1998));
		movielensToIMDbDictionary.put(pair("Velocity of Gary, The", 1998), pair("The Velocity of Gary* *(Not His Real Name)", 1998));
		movielensToIMDbDictionary.put(pair("Allan Quartermain and the Lost City of Gold", 1987), pair("Allan Quatermain and the Lost City of Gold", 1986));
		movielensToIMDbDictionary.put(pair("Marcello Mastroianni: I Remember Yes, I Remember", 1997), pair("Marcello Mastroianni: mi ricordo, sì, io mi ricordo", 1997));
		movielensToIMDbDictionary.put(pair("Very Thought of You, The", 1998), pair("Martha - Meet Frank, Daniel and Laurence", 1998));
		movielensToIMDbDictionary.put(pair("West Beirut (West Beyrouth)", 1998), pair("West Beyrouth (À l'abri les enfants)", 1998));
		movielensToIMDbDictionary.put(pair("White Boys", 1999), pair("Whiteboyz", 1999));
		movielensToIMDbDictionary.put(pair("Adventures of Milo and Otis, The", 1986), pair("Koneko monogatari", 1986));
		movielensToIMDbDictionary.put(pair("Don't Look in the Basement!", 1973), pair("The Forgotten", 1973));
		movielensToIMDbDictionary.put(pair("Operation Condor (Feiying gaiwak)", 1990), pair("Fei ying gai wak", 1991));
		movielensToIMDbDictionary.put(pair("Operation Condor 2 (Longxiong hudi)", 1990), pair("Lung hing foo dai", 1990));
		movielensToIMDbDictionary.put(pair("Sanjuro", 1962), pair("Tsubaki Sanjûrô", 1962));
		movielensToIMDbDictionary.put(pair("Fistful of Dollars, A", 1964), pair("Per un pugno di dollari", 1964));
		movielensToIMDbDictionary.put(pair("Hard 8 (a.k.a. Sydney, a.k.a. Hard Eight)", 1996), pair("Sydney", 1996));
		movielensToIMDbDictionary.put(pair("My Best Fiend (Mein liebster Feind)", 1999), pair("Mein liebster Feind - Klaus Kinski", 1999));
		movielensToIMDbDictionary.put(pair("Goodbye, 20th Century (Zbogum na dvadesetiot vek)", 1998), pair("Zbogum na dvaesetiot vek", 1998));
		movielensToIMDbDictionary.put(pair("March of the Wooden Soldiers (a.k.a. Laurel & Hardy in Toyland)", 1934), pair("Babes in Toyland", 1934));
		movielensToIMDbDictionary.put(pair("Fantasia 2000", 1999), pair("Fantasia/2000", 1999));
		movielensToIMDbDictionary.put(pair("Terrorist, The (Malli)", 1998), pair("Theeviravaathi: The Terrorist", 1998));
		movielensToIMDbDictionary.put(pair("Women on the Verge of a Nervous Breakdown", 1988), pair("Mujeres al borde de un ataque de nervios", 1988));
		movielensToIMDbDictionary.put(pair("Pokémon: The First Movie", 1998), pair("Gekijô-ban poketto monsutâ - Myûtsû no gyakushû", 1998));
		movielensToIMDbDictionary.put(pair("Messenger: The Story of Joan of Arc, The", 1999), pair("Joan of Arc", 1999));
		movielensToIMDbDictionary.put(pair("Meatballs III", 1987), pair("Meatballs III: Summer Job", 1986));
		movielensToIMDbDictionary.put(pair("Quest for Fire", 1981), pair("La guerre du feu", 1981));
		movielensToIMDbDictionary.put(pair("Yojimbo", 1961), pair("Yôjinbô", 1961));
		movielensToIMDbDictionary.put(pair("Slaughterhouse 2", 1988), pair("Slaughterhouse Rock", 1988));
		movielensToIMDbDictionary.put(pair("Drunken Master (Zui quan)", 1979), pair("Nan bei zui quan", 1979));
		movielensToIMDbDictionary.put(pair("Perfect Blue", 1997), pair("Pafekuto buru", 1997));
		movielensToIMDbDictionary.put(pair("Mighty Peking Man (Hsing hsing wang)", 1977), pair("Xing xing wang", 1977));
		movielensToIMDbDictionary.put(pair("Ballad of Narayama, The (Narayama Bushiko)", 1958), pair("Narayama bushikô", 1958));
//		movielensToIMDbDictionary.put(pair("", ), pair("", ));
		
		//½
		movielensToIMDbDictionary.put(pair("Naked Gun 2 1/2: The Smell of Fear, The", 1991), pair("The Naked Gun 2½: The Smell of Fear", 1991));
		movielensToIMDbDictionary.put(pair("8 1/2 Women", 1999), pair("8 ½ Women", 1999));
		movielensToIMDbDictionary.put(pair("8 1/2", 1963), pair("8½", 1963));//try fixing 1/2
		
		//numbers
		movielensToIMDbDictionary.put(pair("Around the World in 80 Days", 1956), pair("Around the World in Eighty Days", 1956));
		movielensToIMDbDictionary.put(pair("101 Dalmatians", 1961), pair("One Hundred and One Dalmatians", 1961));
		movielensToIMDbDictionary.put(pair("187", 1997), pair("One Eight Seven", 1997));
		movielensToIMDbDictionary.put(pair("Gone in 60 Seconds", 2000), pair("Gone in Sixty Seconds", 2000));
		movielensToIMDbDictionary.put(pair("Johnny 100 Pesos", 1993), pair("Johnny cien pesos", 1994));
		movielensToIMDbDictionary.put(pair("Two Friends", 1986), pair("2 Friends", 1986));
		movielensToIMDbDictionary.put(pair("Jennifer 8", 1992), pair("Jennifer Eight", 1992));
		movielensToIMDbDictionary.put(pair("$1,000,000 Duck", 1971), pair("The Million Dollar Duck", 1971));
		
		//join the words:
		movielensToIMDbDictionary.put(pair("Mouse Hunt", 1997), pair("Mousehunt", 1997));
		movielensToIMDbDictionary.put(pair("Rocket Man", 1997), pair("RocketMan", 1997));
		movielensToIMDbDictionary.put(pair("Time Code", 2000), pair("Timecode", 2000));
		movielensToIMDbDictionary.put(pair("Back Stage", 2000), pair("Backstage", 2000));
		movielensToIMDbDictionary.put(pair("Puppet Master", 1989), pair("Puppetmaster", 1989));
		
		movielensToIMDbDictionary.put(pair("Heavyweights", 1994), pair("Heavy Weights", 1995));
		movielensToIMDbDictionary.put(pair("Texas Chainsaw Massacre, The", 1974), pair("The Texas Chain Saw Massacre", 1974));
		
		movielensToIMDbDictionary.put(pair("Waking Ned Devine", 1998), pair("Waking Ned", 1998));
		
		movielensToIMDbDictionary.put(pair("William Shakespeare's Romeo and Juliet", 1996), pair("Romeo + Juliet", 1996));
		movielensToIMDbDictionary.put(pair("Robert A. Heinlein's The Puppet Masters", 1994), pair("The Puppet Masters", 1994));
		movielensToIMDbDictionary.put(pair("Bram Stoker's Dracula", 1992), pair("Dracula", 1992));
		movielensToIMDbDictionary.put(pair("Wes Craven's New Nightmare", 1994), pair("New Nightmare", 1994));
		movielensToIMDbDictionary.put(pair("Monty Python's Life of Brian", 1979), pair("Life of Brian", 1979));//try searching for title after 's
		
		//won't fix:
		movielensToIMDbDictionary.put(pair("Smiling Fish and Goat on Fire", 1999), pair("Goat on Fire and Smiling Fish", 1999));
		movielensToIMDbDictionary.put(pair("I Am Cuba (Soy Cuba/Ya Kuba)", 1964), pair("Soy Cuba", 1964));
		movielensToIMDbDictionary.put(pair("Halloween: H20", 1998), pair("Halloween H20: 20 Years Later", 1998));
		movielensToIMDbDictionary.put(pair("My Left Foot", 1989), pair("My Left Foot: The Story of Christy Brown", 1989));
		movielensToIMDbDictionary.put(pair("Hellhounds on My Trail", 1999), pair("Hellhounds on My Trail: The Afterlife of Robert Johnson", 2000));//try fixing :
		movielensToIMDbDictionary.put(pair("Boys Life", 1995), pair("Boys Life: Three Stories of Love, Lust, and Liberation", 1994));
		movielensToIMDbDictionary.put(pair("Scream of Stone (Schrei aus Stein)", 1991), pair("Cerro Torre: Schrei aus Stein", 1991));
		movielensToIMDbDictionary.put(pair("Broken Hearts Club, The", 2000), pair("The Broken Hearts Club: A Romantic Comedy", 2000));
		movielensToIMDbDictionary.put(pair("Lodger, The", 1926), pair("The Lodger: A Story of the London Fog", 1927));
		movielensToIMDbDictionary.put(pair("Hands on a Hard Body", 1996), pair("Hands on a Hard Body: The Documentary", 1997));
		movielensToIMDbDictionary.put(pair("Crime and Punishment in Suburbia", 2000), pair("Crime + Punishment in Suburbia", 2000));//try fixing and to '+'

		//typos
		movielensToIMDbDictionary.put(pair("Adventures of Buckaroo Bonzai Across the 8th Dimension, The", 1984), pair("The Adventures of Buckaroo Banzai Across the 8th Dimension", 1984));
		movielensToIMDbDictionary.put(pair("Caligula", 1980), pair("Caligola", 1979));
		movielensToIMDbDictionary.put(pair("Three Amigos!", 1986), pair("¡Three Amigos!", 1986));
		
		//different year:
		movielensToIMDbDictionary.put(pair("Fantastic Night, The (La Nuit Fantastique)", 1949), pair("La nuit fantastique", 1942));
		movielensToIMDbDictionary.put(pair("It Happened Here", 1961), pair("It Happened Here", 1965));
		movielensToIMDbDictionary.put(pair("Henry: Portrait of a Serial Killer", 1990), pair("Henry: Portrait of a Serial Killer", 1986));
		
		//maybe fix:
		movielensToIMDbDictionary.put(pair("Concorde: Airport '79, The", 1979), pair("The Concorde... Airport '79", 1979));
		movielensToIMDbDictionary.put(pair("X-Files: Fight the Future, The", 1998), pair("The X Files", 1998));
		movielensToIMDbDictionary.put(pair("Baby... Secret of the Lost Legend", 1985), pair("Baby: Secret of the Lost Legend", 1985));
		movielensToIMDbDictionary.put(pair("Love Is the Devil", 1998), pair("Love Is the Devil: Study for a Portrait of Francis Bacon", 1998));
		movielensToIMDbDictionary.put(pair("G. I. Blues", 1960), pair("G.I. Blues", 1960));//try removing '.' and fixing spaces
		movielensToIMDbDictionary.put(pair("Besieged (L' Assedio)", 1998), pair("L'assedio", 1998));
		movielensToIMDbDictionary.put(pair("School of Flesh, The (L' École de la chair)", 1998), pair("L'école de la chair", 1998));//fix space after apostrophe
		
		movielensToIMDbDictionary.put(pair("Farinelli: il castrato", 1994), pair("Farinelli", 1994));
		movielensToIMDbDictionary.put(pair("Tales from the Crypt Presents: Bordello of Blood", 1996), pair("Bordello of Blood", 1996));//try splitting on ':'
		movielensToIMDbDictionary.put(pair("Santa Claus: The Movie", 1985), pair("Santa Claus", 1985));
		movielensToIMDbDictionary.put(pair("Jerry Springer: Ringmaster", 1998), pair("Ringmaster", 1998));
		
		movielensToIMDbDictionary.put(pair("East-West (Est-ouest)", 1999), pair("Est - Ouest", 1999));//try fixing spaces around '-'
		movielensToIMDbDictionary.put(pair("Lords of Flatbush, The", 1974), pair("The Lord's of Flatbush", 1974));//try removing quote '
		movielensToIMDbDictionary.put(pair("Crocodile Dundee II", 1988), pair("'Crocodile' Dundee II", 1988));//try fix apostrophes
		movielensToIMDbDictionary.put(pair("Breaker Morant", 1980), pair("'Breaker' Morant", 1980));
		
		//roman numerals
		movielensToIMDbDictionary.put(pair("Fright Night Part II", 1989), pair("Fright Night Part 2", 1988));//try fixing roman numbers
		movielensToIMDbDictionary.put(pair("Mission: Impossible 2", 2000), pair("Mission: Impossible II", 2000));
    }
	
	private boolean fixYears = false;
	
	private static Pair<String, Integer> pair(String name, Integer year) {
		return new Pair<String, Integer>(name, year);
	}
	
	public MovieCollection() {
		movies = new HashMap<>();
	}
	
	private Movie<T> getTranslatedMovie(String name, Integer year) {
		Pair<String, Integer> translation = movielensToIMDbDictionary.get(pair(name, year));
		if (translation != null) {
			Movie<T> movie = getNotTranslatedMovie(translation.getFirst(), translation.getSecond());
			if (movie != null) {
				return movie;
			}
		}
		translation = movielensToIMDbDictionary2.get(pair(name, year));
		if (translation != null) {
			Movie<T> movie = getNotTranslatedMovie(translation.getFirst(), translation.getSecond());
			if (movie != null) {
				return movie;
			}
		}
		return null;
	}

	public Movie<T> getMovie(String name, Integer year) {
		Movie<T> movie = getNotTranslatedMovie(name, year);
		if (movie != null) {
			return movie;
		} else {
			return getTranslatedMovie(name, year);
		}
	}
	
	private Movie<T> getNotTranslatedMovie(String name, Integer year) {
		String standardName = standardizeName(name);
		Movie<T> movieInternal = getMovieInternal(standardName, year);
		if (movieInternal != null) {
			return movieInternal;
		} else {
			return doFixing(standardName, year);
		}
	}

	public Movie<T> getOrCreateMovie(String name, Integer year) {
		fixYears = false;
		Movie<T> movie = getMovie(name, year);
		fixYears = true;
		if (movie != null) {
			return movie;
		} else {
			String standardizedName = standardizeName(name);
			MovieId m = new MovieId(standardizedName, year);
			Movie<T> newMovie = new Movie<T>();
			movies.put(m, newMovie);
			return newMovie;
		}
	}


	private Movie<T> getMovieInternal(String name, Integer year) {
		return processParenthesis(name, year);
	}
	
	private Movie<T> processParenthesis(String name, Integer year) {
		if (containsEmptyParenthesis(name)) {
			return processParenthesis(name.replace("()", ""), year);
		} else if (isInParenthesis(name)) {
			return processParenthesis(name.substring(1, name.length() - 1), year);
		} else if (containsParenthesis(name)) {
			String[] names = splitOnParenthesis(name);
			Movie<T> movie1 = processParenthesis(names[0], year);
			if (movie1 != null) {
				return movie1;
			} else {
				Movie<T> movie2 = processParenthesis(names[1], year);
				return movie2;
			}
		} else {
			return processArticles(name, year);
		}
	}
	
	private static boolean containsEmptyParenthesis(String name) {
		return name.contains("()");
	}
	
	private boolean isInParenthesis(String name) {
		return name.matches("\\(.*\\)");
	}
	
	private static boolean containsParenthesis(String name) {
		return name.matches(".+\\(.+\\)");
	}
	
	private static String[] splitOnParenthesis(String name) {
		Pattern pattern = Pattern.compile("(.+)\\((.+)\\)");
		Matcher matcher = pattern.matcher(name);
		if (matcher.matches()) {
			return new String[] {matcher.group(1).trim(), matcher.group(2).trim()};
		} else {
			throw new IllegalStateException("name should contain parenthesis: " + name);
		}
	}
	
	private Movie<T> processArticles(String name, Integer year) {
		if (endsWithArticle(name)) {
			return processWithoutEndArticle(moveArticleToTheBeginning(name), year);
		} else {
			return processWithoutEndArticle(name, year);
		}
	}
	
	private boolean endsWithArticle(String name) {
		for (int i = 0; i < endArticles.length; i++) {
			if (name.endsWith(endArticles[i])) {
				return true;
			}
		}
		return false;
	}
	
	private String moveArticleToTheBeginning(String name) {
		for (int i = 0; i < endArticles.length; i++) {
			if (name.endsWith(endArticles[i])) {
				String alteredName = startArticles[i] +  name.substring(0, name.length() - endArticles[i].length());
				alteredName = alteredName.replaceAll(",", "");
				return alteredName;
			}
		}
		throw new IllegalStateException("should contain article at the end: " + name);
	}
	
	private Movie<T> processWithoutEndArticle(String name, Integer year) {
		if (startsWithArticle(name)) {
			Movie<T> movie = doFixing(name, year);
			if (movie != null) {
				return movie;
			} else {
				return doFixing(removeArticleFromBeginning(name), year);
			}
		} else {
			Movie<T> movie = doFixing(name, year);
			if (movie != null) {
				return movie;
			} else {
				return tryAddingArticleToBeginning(name, year);
			}
		}
	}
	
	/**
	 * Tries to add a character to the beginning of the title and then find this movie in
	 * the collection of movies.
	 * @return Movie found or null.
	 */
	private Movie<T> tryAddingArticleToBeginning(String name, Integer year) {
		for (int i = 0; i < startArticles.length; i++) {
			Movie<T> movie = doFixing(startArticles[i] + name, year);
			if (movie != null) {
				return movie;
			}
		}
		return null;
	}
	
	/**
	 * Returns true iff the title starts with any recognized article.
	 */
	private boolean startsWithArticle(String name) {
		for (int i = 0; i < startArticles.length; i++) {
			if (name.startsWith(startArticles[i])) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Removes the article from the beginning of the title.
	 * The title must contain article, otherwise {@link IllegalStateException} is thrown.
	 */
	private String removeArticleFromBeginning(String name) {
		for (int i = 0; i < startArticles.length; i++) {
			if (name.startsWith(startArticles[i])) {
				return name.replaceFirst(startArticles[i], "");
			}
		}
		throw new IllegalStateException("should begin with article: " + name);
	}
	
	private Movie<T> doFixing(String name, Integer year) {
		return tryFixAmpersand(name, year);
	}
	
	private Movie<T> tryFixAmpersand(String name, Integer year) {
		if (name.contains(" & ")) {
			String fixedName = name.replaceAll(" & ", " and ");
			Movie<T> movie = tryFixYear(fixedName, year);
			if (movie != null) {
				return movie;
			}
		}
		if (name.contains(" and ")) {
			String fixedName = name.replaceAll(" and ", " & ");
			Movie<T> movie = tryFixYear(fixedName, year);
			if (movie != null) {
				return movie;
			}
		}
		return tryFixYear(name, year);
	}
	
	/**
	 * Trying to match a movie first on provided year and then on +1, -1, +2, -2, +3, -3 years.
	 * This is due to fact that IMDb and Movielens years of production may differ and actually do
	 * in a number of cases.
	 */
	private Movie<T> tryFixYear(String name, Integer year) {
		if (fixYears == false) {
			return movies.get(new MovieId(name, year));
		}
		
		if (year == null) {
			return null;
		}
		Integer[] yearFixes = {0, 1, -1, 2, -2, 3, -3};
		for (int i = 0; i < yearFixes.length; i++) {
			Movie<T> movieYearFixed = movies.get(new MovieId(name, year + yearFixes[i]));
			if (movieYearFixed != null) {
				return movieYearFixed;
			}
		}
		return null;
	}

	public Set<Movie<T>> getMovies() {
		return new HashSet<Movie<T>>(movies.values());
	}
	
	/**
	 * Processing of the title of the movie to be in a standard form, meaning: removing accents,
	 * removing some special characters, removing some special tokens which appear in titles.
	 */
	protected static String standardizeName(String name) {
		String deaccented = deAccent(name);
		deaccented = deaccented.toLowerCase();
		deaccented = deaccented.trim();
		deaccented = deaccented.replace("!", "");
		deaccented = deaccented.replace("\"", "");
		deaccented = deaccented.replace("?", "");
		deaccented = deaccented.replace(":", "");
		deaccented = deaccented.replaceAll("\\s*/\\*", " ");
		deaccented = deaccented.replaceAll("a\\.k\\.a\\.\\s*", "");
		deaccented = deaccented.replaceAll("\\s*\\.\\s*", " ");
		deaccented = deaccented.replaceAll("\\s*\\-\\s*", " ");
		deaccented = deaccented.replaceAll("\\s-\\s", "");
		return removeCommasNotBeforeArticle(deaccented);
	}
	
	/**
	 * Removes comma everywhere in the input string, except from the position right before
	 * any article at the end of title.
	 */
	protected static String removeCommasNotBeforeArticle(String name) {
		if (containsParenthesis(name)) {
			String[] names = splitOnParenthesis(name);
			return removeCommasNotBeforeArticle(names[0]) + " (" + removeCommasNotBeforeArticle(names[1]) + ")";
		}
		for (int i = 0; i < endArticles.length; i++) {
			if (name.toLowerCase().endsWith(endArticles[i])) {
				int nameLength = name.length();
				int articleWithCommaLength = endArticles[i].length();
				String withoutArticle = name.substring(0, nameLength - articleWithCommaLength);
				withoutArticle = withoutArticle.replaceAll(",", "");
				return withoutArticle + name.subSequence(nameLength - articleWithCommaLength, nameLength);
			}
		}
		return name.replaceAll(",", "");
	}
	
	/**
	 * Remove accents from national characters and translate them to standard English alphabet characters.
	 */
	protected static String deAccent(String str) {
	    String nfdNormalizedString = Normalizer.normalize(str, Normalizer.Form.NFD); 
	    Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
	    return pattern.matcher(nfdNormalizedString).replaceAll("");
	}
	
}
