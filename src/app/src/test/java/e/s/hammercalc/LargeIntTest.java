package e.s.hammercalc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import e.s.hammercalc.core.LargeInt;

public class LargeIntTest {
    @Test
    public void can_create_large_ints_from_primitive_ints(){
        LargeInt negOneInt = LargeInt.fromInt(-1);
        assertTrue("negOneInt matches NEG_ONE constant", negOneInt.equals(LargeInt.NEG_ONE));
        assertFalse("negOneInt not isNan", negOneInt.isNaN());
        assertTrue("negOneInt valid", negOneInt.isValid());

        LargeInt longOne = LargeInt.fromLong(1L);
        assertTrue("longOne matches ONE constant", longOne.equals(LargeInt.ONE));
        assertFalse("longOne not isNan", longOne.isNaN());
        assertTrue("longOne valid", longOne.isValid());

        LargeInt intMaxInt = LargeInt.fromInt(Integer.MAX_VALUE);
        LargeInt intMaxStr = new LargeInt("7FFFFFFF", 16);
        assertTrue("intMaxInt matches string version", intMaxInt.equals(intMaxStr));
        assertFalse("intMaxInt not isNan", intMaxInt.isNaN());
        assertTrue("intMaxInt valid", intMaxInt.isValid());

        LargeInt longMaxLong = LargeInt.fromLong(Long.MAX_VALUE);
        LargeInt longMaxStr = new LargeInt("7FFFFFFFFFFFFFFF", 16);
        assertTrue("longMaxLong matches string version", longMaxLong.equals(longMaxStr));
        assertFalse("longMaxLong not isNan", longMaxLong.isNaN());
        assertTrue("longMaxLong valid", longMaxLong.isValid());
    }

    @Test
    public void can_create_basic_value_large_ints_from_base_10_strings_that_match_constants() {
        LargeInt neg_one = new LargeInt("-1");
        assertTrue("matches NEG_ONE constant", neg_one.equals(LargeInt.NEG_ONE));
        assertFalse("NEG_ONE not isNan", neg_one.isNaN());
        assertTrue("NEG_ONE valid", neg_one.isValid());

        LargeInt zero = new LargeInt("0");
        assertTrue("matches ZERO constant", zero.equals(LargeInt.ZERO));
        assertFalse("ZERO not isNan", zero.isNaN());
        assertTrue("ZERO valid", zero.isValid());

        LargeInt one = new LargeInt("1");
        assertTrue("matches ONE constant", one.equals(LargeInt.ONE));
        assertFalse("ONE not isNan", one.isNaN());
        assertTrue("ONE valid", one.isValid());

        LargeInt two = new LargeInt("2");
        assertTrue("matches TWO constant", two.equals(LargeInt.TWO));
        assertFalse("TWO not isNan", two.isNaN());
        assertTrue("TWO valid", two.isValid());
    }

    @Test
    public void can_create_basic_value_large_ints_from_base_16_strings() {
        LargeInt strHex = new LargeInt("FFFF", 16);
        LargeInt strDec = new LargeInt("65535", 10);
        LargeInt intDec = LargeInt.valueOf(65535);

        assertTrue("hex matches int value", strHex.equals(intDec));
        assertTrue("dec matches hex value", strDec.equals(strHex));
    }

    @Test
    public void can_create_negative_value_large_ints_from_base_16_strings() {
        LargeInt strHex = new LargeInt("-FFFF", 16);
        LargeInt strDec = new LargeInt("-65535", 10);
        LargeInt intDec = LargeInt.valueOf(-65535);

        assertTrue("hex matches int value", strHex.equals(intDec));
        assertTrue("dec matches hex value", strDec.equals(strHex));
    }

    @Test
    public void invalid_operations_and_empty_strings_give_NAN(){
        LargeInt blank = new LargeInt("");
        assertTrue("blank string matches LARGE_NAN constant", blank.equals(LargeInt.LARGE_NAN));
        assertTrue("blank string isNan", blank.isNaN());
        assertFalse("blank string not valid", blank.isValid());

        LargeInt divZero = LargeInt.TWO.divide(LargeInt.ZERO);
        assertTrue("divZero matches LARGE_NAN constant", divZero.equals(LargeInt.LARGE_NAN));
        assertTrue("divZero isNan", divZero.isNaN());
        assertFalse("divZero not valid", divZero.isValid());
    }

    @Test
    public void large_integer_strings_round_trip_correctly__base10(){
        // the 12345th Fibonacci number: 2580 characters
        String largePositiveStr = "400805695072240470970514993214065752192289440772063392234116121035966330621821050108284603033716632771086638046166577665205834362327397885009536790892524821512145173749742393351263429067658996935575930135482780507243981402150702461932551227590433713277255705297537428017957026536279252053237729028633507123483103210846617774763936154673522664591736081039709294423865668046925492747583953758325850613548914282578320544573036249175099094644435323970587790740267131607004023987409385716162460955707793257532112771932704816713519196128834470721836094265012918046427449156654067195071358955104097973710150920536847877434256779886729555691213282504703193401739340461924048504866698176130757935914248753973087073009601101912877383634628929467608983980664185363370286731771712542583041365328648124549323878806758395652340861186334027392307091079257180835672989798524084534677252369585918458720952520972332496025465803523315515681084895362126005441170936820059518262349022456888758938672920855739736423917065122816343192172271301981007636070751378441363091187289522144227851382197807194256392294919912037019476582418451273767976783751999133072126657949249799858935787018952232743400610036315564885371356712960608966755186612620425868892621106627825137425386831657368826398245606147944273998498356443362170133234924531673939303668042878258282104212769625245680321344034442698232414181912301904509531018692483863038992377680591406376081935756597411807864832452421993121459549055042253305545594009110753730302061881025182053074077930494574304284381890534053065639084253641881363463311184024281835265103884539012874542416238100890688593076189105555658375552988619203325356676814545718066196038345684671830102920209857682912971565838896011294918349088792184108318689299230788355618638040186790724351073650210514429114905535411044888774713860041341593318365792673354888566799196442017231870631867558530906286613228902689695061557951752309687806567573290910909535395758148994377158637050112347651517847188123790794231572729345617619677555583207012253101701328971768827861922408064379891201972881554890367344239218306050355964382953279316318309272212482218232309006973312977359562553184608144571713073802285675503209229581312057259729362382786183100343961484090866057560474044189870633912200595478051573769889968342203512550302655117491740823696686983281784153050366346823513213598551985596176977626982962058849363351794302206703907577970065793839511591930741441079234179943480206539767561244271325923343752071038968002157889912694947204003637791271084190929058369801531787887444598295425899927970";
        LargeInt largePositive = new LargeInt(largePositiveStr);

        assertEquals("is positive", 1, largePositive.sign());

        assertFalse("NEG_ONE not isNan", largePositive.isNaN());
        assertTrue("NEG_ONE valid", largePositive.isValid());

        String largePositiveResult = largePositive.toString();
        assertEquals("positive string should match", largePositiveStr, largePositiveResult);

        // concatenation of OEIS sequence
        String largeNegativeStr = "-28446376921171241721882362682752792843163323874124285085245495566036046526687117167647757968448738929089279561004102510521084113212281244125113241359138814131421";
        LargeInt largeNegative = new LargeInt(largeNegativeStr);

        assertEquals("is negative", -1, largeNegative.sign());

        assertFalse("NEG_ONE not isNan", largeNegative.isNaN());
        assertTrue("NEG_ONE valid", largeNegative.isValid());

        String largeNegativeResult = largeNegative.toString();
        assertEquals("positive string should match", largeNegativeStr, largeNegativeResult);
    }

    @Test
    public void large_integer_strings_round_trip_correctly__base16(){
        String largePositiveStr = "f01dab1efacebeeffeedc1a551f1ab1e";
        LargeInt largePositive = new LargeInt(largePositiveStr, 16);

        assertEquals("is positive", 1, largePositive.sign());

        assertFalse("NEG_ONE not isNan", largePositive.isNaN());
        assertTrue("NEG_ONE valid", largePositive.isValid());

        String largePositiveResult = largePositive.toString(16);
        assertEquals("positive string should match", largePositiveStr, largePositiveResult);

        String largeNegativeStr = "-f01dab1efacebeeffeedc1a551f1ab1e";
        LargeInt largeNegative = new LargeInt(largeNegativeStr, 16);

        assertEquals("is negative", -1, largeNegative.sign());

        assertFalse("NEG_ONE not isNan", largeNegative.isNaN());
        assertTrue("NEG_ONE valid", largeNegative.isValid());

        String largeNegativeResult = largeNegative.toString(16);
        assertEquals("positive string should match", largeNegativeStr, largeNegativeResult);
    }

    @Test
    public void can_compare_large_integers_in_order(){
        LargeInt a = new LargeInt("-10000000000000000000");
        LargeInt b = new LargeInt("-10");
        LargeInt b1 = new LargeInt("-10");
        LargeInt c = new LargeInt("0");
        LargeInt d = new LargeInt("10");
        LargeInt e = new LargeInt("10000000000000000000");

        assertEquals("a < b", -1, a.compareTo(b));
        assertEquals("b = b", 0, b.compareTo(b1));
        assertEquals("b > a", 1, b.compareTo(a));
        assertEquals("c > b", 1, c.compareTo(b));
        assertEquals("b < c", -1, b.compareTo(c));
        assertEquals("d > c", 1, d.compareTo(c));
        assertEquals("c < d", -1, c.compareTo(d));
        assertEquals("e > a", 1, e.compareTo(a));
        assertEquals("a < e", -1, a.compareTo(e));
    }

    @Test
    public void can_create_a_large_int_with_random_bits_of_a_given_length(){
        LargeInt randomLargeInt = LargeInt.randomBits(100);

        assertTrue("is sufficiently long", randomLargeInt.bitLength() > 50);
        assertTrue("has a scatter of values", randomLargeInt.bitCount() > 20);
        assertEquals("is positive", 1, randomLargeInt.sign());

        System.out.println(randomLargeInt);
    }

    @Test
    public void can_read_bit_lengths_and_positions() {
        // 83C0 -> b1000001111000000 : 5 bits set, 16 bits to express
        LargeInt val = new LargeInt("83C0000083C083C083C0000083C083C0", 16);

        assertEquals("bitLength", 128, val.bitLength()); // number of bits needed to express
        assertEquals("bitCount", 30, val.bitCount()); // number of bits set to 1
        assertEquals("getLowestSetBit", 6, val.getLowestSetBit()); // offset of least significant 1; zero-based

        assertFalse("bit 6", val.testBit(5));
        assertTrue("bit 7", val.testBit(6));
        assertTrue("bit 8", val.testBit(7));
        assertTrue("bit 9", val.testBit(8));
        assertTrue("bit 10", val.testBit(9));
        assertFalse("bit 11", val.testBit(10));
    }

    @Test
    public void can_serialise_and_restore_positive_large_int_through_a_byte_array(){
        String original = "90210569507224047097051499321406575219228944077206339223411612103596633062";
        LargeInt largePositive = new LargeInt(original);

        byte[] compact = largePositive.toStorage();

        for (byte b: compact){
            System.out.print(b);
            System.out.print(',');
        }
        System.out.println();

        LargeInt recovered = LargeInt.fromStorage(compact);
        System.out.println(original);
        System.out.println(recovered);

        assertTrue("resulted in same value", largePositive.equals(recovered));
    }

    @Test
    public void can_serialise_and_restore_negative_large_int_through_a_byte_array(){
        String original = "-90210569507224047097051499321406575219228944077206339223411612103596633062";
        LargeInt largeNegative = new LargeInt(original);

        byte[] compact = largeNegative.toStorage();

        for (byte b: compact){
            System.out.print(b);
            System.out.print(',');
        }
        System.out.println();

        LargeInt recovered = LargeInt.fromStorage(compact);
        System.out.println(original);
        System.out.println(recovered);

        assertTrue("resulted in same value", largeNegative.equals(recovered));
    }

    @Test
    public void can_get_a_byte_array_of_twos_compliment_value(){
        LargeInt negative = new LargeInt("-FFFFF001", 16);
        LargeInt positive = new LargeInt( "FFFFF001", 16);

        byte[] compact_n = negative.toByteArray();
        byte[] compact_p = positive.toByteArray();

        System.out.print("Negative bytes: ");
        for (byte b: compact_n){
            System.out.print(b);
            System.out.print(',');
        }
        System.out.print("\r\nPositive bytes: ");
        for (byte b: compact_p){
            System.out.print(b);
            System.out.print(',');
        }
        System.out.println();

        LargeInt recovered_p = LargeInt.fromByteArray(compact_p); // always assumes positive
        assertEquals("2s compliment positive", "4294963201", recovered_p.toString());

        LargeInt recovered_n = LargeInt.fromByteArray(compact_n); // always assumes positive
        assertEquals("2s compliment negative", "4095", recovered_n.toString());
    }

    @Test
    public void abs_gives_absolute_value(){
        LargeInt a = new LargeInt("-1023");
        LargeInt b = new LargeInt("1023");

        assertEquals("positive and abs", b, b.abs());
        assertEquals("negative and abs", b, a.abs());
    }

    @Test
    public void add_sums_values(){
        LargeInt a = new LargeInt("384626433832795028841971693993");
        LargeInt b = new LargeInt("92307816406286208998628034825");
        LargeInt nb = new LargeInt("-92307816406286208998628034825");
        LargeInt dnb = new LargeInt("-184615632812572417997256069650");

        LargeInt aPlusB = new LargeInt("476934250239081237840599728818");
        LargeInt aPlusNb = new LargeInt("292318617426508819843343659168");


        assertEquals("a + b", aPlusB, a.add(b));
        assertEquals("b + a", aPlusB, b.add(a));
        assertEquals("a + (-b)",aPlusNb, a.add(nb));
        assertEquals("(-b) + a",aPlusNb, nb.add(a));
        assertEquals("(-b) + (-b)",dnb, nb.add(nb));
        assertEquals("b + (-b)",LargeInt.ZERO, b.add(nb));
    }

    @Test
    public void divide_gives_floor_of_quotient(){
        LargeInt a = new LargeInt("384626433832795028841971693993");
        LargeInt b = new LargeInt("92307816406286208998628034825");
        LargeInt nb = new LargeInt("-92307816406286208998628034825");

        LargeInt aOverB = new LargeInt("4");
        LargeInt aOverNb = new LargeInt("-4");
        LargeInt bOverA = new LargeInt("0");
        LargeInt nbOverA = new LargeInt("0");

        assertEquals("a / 1", a, a.divide(LargeInt.ONE));
        assertEquals("a / b", aOverB, a.divide(b));
        assertEquals("a / (-b)", aOverNb, a.divide(nb));
        assertEquals("b / a",bOverA, b.divide(a));
        assertEquals("(-b) / a",nbOverA, nb.divide(a));
        assertEquals("a / a",LargeInt.ONE, a.divide(a));
        assertEquals("b / b",LargeInt.ONE, b.divide(b));
        assertEquals("-b / -b",LargeInt.ONE, nb.divide(nb));
    }

    @Test
    public void divide_by_zero_gives_NaN_value(){
        LargeInt a = new LargeInt("384626433832795028841971693993");

        assertEquals("a / 0", LargeInt.LARGE_NAN, a.divide(LargeInt.ZERO));
    }

    @Test
    public void divideAndRemainder_gives_quotient_and_remainder(){
        LargeInt a = new LargeInt("384626433832795028841971693993");
        LargeInt aPlus1 = new LargeInt("384626433832795028841971693994");
        LargeInt b = new LargeInt("9230781640628620");
        LargeInt nb = new LargeInt("-9230781640628620");

        LargeInt aOverB_Q = new LargeInt("41667807646959");
        LargeInt aOverB_R = new LargeInt("1072566180327413");
        LargeInt aOverNb_Q = new LargeInt("-41667807646959");

        assertEquals("a / 1 Q", a, a.divideAndRemainder(LargeInt.ONE)[0]);
        assertEquals("a / 1 R", LargeInt.ZERO, a.divideAndRemainder(LargeInt.ONE)[1]);

        assertEquals("(a+1) / a Q", LargeInt.ONE, aPlus1.divideAndRemainder(a)[0]);
        assertEquals("(a+1) / a R", LargeInt.ONE, aPlus1.divideAndRemainder(a)[1]);

        assertEquals("a / b Q", aOverB_Q, a.divideAndRemainder(b)[0]);
        assertEquals("a / b R", aOverB_R, a.divideAndRemainder(b)[1]);
        assertEquals("a / b double-check", a, b.multiply(aOverB_Q).add(aOverB_R));

        assertEquals("a / (-b) Q", aOverNb_Q, a.divideAndRemainder(nb)[0]);
        assertEquals("a / (-b) R", aOverB_R, a.divideAndRemainder(nb)[1]); // remainder always positive

        assertEquals("b / a Q", LargeInt.ZERO, b.divideAndRemainder(a)[0]);
        assertEquals("b / a R", b, b.divideAndRemainder(a)[1]);

        assertEquals("(-b) / a Q", LargeInt.ZERO, nb.divideAndRemainder(a)[0]);
        assertEquals("(-b) / a R", nb, nb.divideAndRemainder(a)[1]); // remainder always positive

        assertEquals("a / a Q",LargeInt.ONE, a.divideAndRemainder(a)[0]);
        assertEquals("a / a R",LargeInt.ZERO, a.divideAndRemainder(a)[1]);

        assertEquals("b / b Q",LargeInt.ONE, b.divideAndRemainder(b)[0]);
        assertEquals("b / b R",LargeInt.ZERO, b.divideAndRemainder(b)[1]);

        assertEquals("-b / -b Q",LargeInt.ONE, nb.divideAndRemainder(nb)[0]);
        assertEquals("-b / -b R",LargeInt.ZERO, nb.divideAndRemainder(nb)[1]);
    }

    @Test
    public void bit_manipulation_and_lowest_bit_test(){
        LargeInt a = new LargeInt("8000000000000000",16);
        LargeInt b = new LargeInt("8000000000000005",16);

        assertEquals("lowest bit in a", 63, a.getLowestSetBit());
        assertEquals("lowest bit in b", 0, b.getLowestSetBit());

        LargeInt br1 = b.shiftRight(1);
        assertEquals("lowest bit in b >> 1", 1, br1.getLowestSetBit());

        LargeInt al20 = a.shiftLeft(20);
        assertEquals("lowest bit in a << 20", 83, al20.getLowestSetBit());
    }

    @Test
    public void greatest_common_denominator_of_large_ints(){
        LargeInt a = new LargeInt("120000161328172060");
        LargeInt b = new LargeInt("9230781640628620");
        LargeInt c = new LargeInt("9230781640628621");

        LargeInt x = new LargeInt("567453");
        LargeInt y = new LargeInt("64256");
        LargeInt ax = x.multiply(a);
        LargeInt ay = y.multiply(a);

        assertEquals("a : b", b, a.gcd(b));
        assertEquals("c : b", LargeInt.ONE, c.gcd(b));
        assertEquals("(a*x : a*y) = a", a, ax.gcd(ay));
    }

    @Test
    public void large_value_truncated_to_long(){
        LargeInt a = new LargeInt( "8000000000000000",16);
        LargeInt b = new LargeInt("80000000F00000000",16);

        long at = a.longValue();
        long bt = b.longValue();

        assertEquals("(long)a", 0x8000000000000000L, at);
        assertEquals("(long)b", 0xF00000000L, bt);
    }

    @Test
    public void pick_the_least_of_two_values(){
        LargeInt a = new LargeInt("120000161328172060");
        LargeInt b = new LargeInt("120000");
        LargeInt c = new LargeInt("-120000161328172");

        assertEquals("min(a,b)", b, a.min(b));
        assertEquals("min(b,a)", b, b.min(a));

        assertEquals("min(a,c)", c, a.min(c));
        assertEquals("min(c,a)", c, c.min(a));

        assertEquals("min(b,c)", c, b.min(c));
        assertEquals("min(c,b)", c, c.min(b));
    }

    @Test
    public void pick_the_larger_of_two_values(){
        LargeInt a = new LargeInt("120000161328172060");
        LargeInt b = new LargeInt("120000");
        LargeInt c = new LargeInt("-120000161328172");

        assertEquals("max(a,b)", a, a.max(b));
        assertEquals("max(b,a)", a, b.max(a));

        assertEquals("max(a,c)", a, a.max(c));
        assertEquals("max(c,a)", a, c.max(a));

        assertEquals("max(b,c)", b, b.max(c));
        assertEquals("max(c,b)", b, c.max(b));
    }

    @Test
    public void modulo_of_large_values(){
        LargeInt a = new LargeInt("384626433832795028841971693993");
        LargeInt aPlus1 = new LargeInt("384626433832795028841971693994");
        LargeInt b = new LargeInt("9230781640628620");

        LargeInt expected1 = new LargeInt("1072566180327413");
        LargeInt expected2 = expected1.add(LargeInt.ONE);

        LargeInt aModB = a.mod(b);
        LargeInt ap1ModB = aPlus1.mod(b);
        LargeInt bModA = b.mod(a);

        assertEquals("a % b", expected1, aModB);
        assertEquals("(a+1) % b", expected2, ap1ModB);
        assertEquals("b % a", b, bModA);
    }

    @Test
    public void modInverse_gives_modular_multiplicative_inverse(){
        LargeInt a = new LargeInt("384626433832795028841971693993");
        LargeInt b = new LargeInt("9230781640628620");

        LargeInt x = a.modInverse(b);
        LargeInt expected = new LargeInt("1203788136256577");
        assertEquals("ax", expected, x);

        LargeInt check = x.multiply(a).mod(b);
        assertEquals("(a * x) % b", LargeInt.ONE, check);
    }

    @Test
    public void modPow_gives_power_in_modular_form(){
        LargeInt a = new LargeInt("7164852");
        LargeInt e = new LargeInt("923");
        LargeInt m = new LargeInt("81640628620");

        // 2271264069394823272787223192942519597282070102900571140244279943227752790871030209826024797995931097073538989272487794342908716986976347857704662465554806777184008346044226885858291052294586213821999951913082631890153653327388198368378707682277279793060760504183639028151195941305006022100232602561484362837432392629914631494518600943618643133134503945875219106446366155061137391295177539903017476771967465242377801084348365579238454791752412404986057133444881218078202939968525537055883638686796485936987520073875369963903489971066706590455703406465751282450464735518124730543867637034482955945205951242777678800955450454180414333270997039274883822987801442782721238271215412335068250606781183923609659856133514148607250660463134676678330733306382423886957326445718910184755778917991507602389289712325699727611378218488625788954810979008673882974942607172114788746958082098489057452134321033018600212083413510652011818342157509499516296155499016701475993616581097624389657935420956495059107117759054159805873767109273749941572742935406706786449578648617587391662739260842675651818027184561602928366394847911589498165926299937684211217075118031199689458803968825088638024145095482158459412744383037598176754396265130572274987368549581659271277636080345476158303833419239790023627460902279555525645518915097783612446941688704730384187887448931323003796088935112865427675093768645715006000475282160482513245813717017327119806897858472745918759118130688084486425252769279647637489785468033744518156613536855048659815365691314746320287084303291148884566557126431309838588335031204539036633382124639933275357414847217974220286240659833263613544782725846996271247751984078422672223406628493134718494968243191969833811996323895012146204083785071529365940660148488798722710650354297670123548879314659891433643584973105902454182500712748590823291538482331138245312626920404965642003315933934218753550374925061479229527719067949835353735324229524921738134120630802730467046500102912666774728137332469392689020355868036925076226509417684053206845387873468048243449300560454860813621834312634918095546400820295210686729693002508395262673610790797759818960472797564893742071386166021290102547193972632271915044220735121945875626395709643182721804318382949674352884309739756622997445763329370058753518120331150429231288754777075380596977654318839330961853704426798143108687100247281808883017386130241204941588073930525165849454817611247546404329362387065175070451344782530098516479505122343125934024933589787237737320129675170293942603722118865568535065093724360572133575311183705425758039757202468762181935934503764254624308072838211958420709023257726828047300954302419157478340257756342195847310722215462370068349154872269125058269713963393219284291565817237293202061808620540309500061652963581703599015540660282451394155320785157848793815504223132233716650226690381837829968721414218794741659593591395263682378244071662341311142541927963054273718212466924758559829273649988912462209334462798514414096672142702110032517258094032969821767658801003191214444317809141426288623034629347710354108135748001264553553020707019934636109673627602375495232781248101792449067908140195529080978567311889358079597564894469637962846488597364304185487827373676069621723206794116960571352703835765068105515619501669506215011326809126914825917453010856133561861938207038709231739412623538970675597625443467365480802647334953250430578523646645319493351783886714935256003310598728689730776214016775273364506547694692260912364300392326937287954604754601145284077820367242530064463686251099104274906911716003743214522020266399566214531720407578821802409790947683767418679585629995355563247094083584383660372851407888240924749609930448355757978607762867276402346744943449860932176395141836871198302119103459169014759948891992464348032255241303100294506743895395553863854617439605769732875631280412462435169258654655896283488894353595333430847779013592761319125867596015791306283903571283837968903550328611960886952348113795046149541649004580923016939745491173195336127081148676069044056449789482515996160854436309419265819001787258307720058113306952478833489758141845315692951370543776998734199081225131882725038355531940411045166660746935541288398352833859635345618446382062870579621866312222478090037543936920930571243886164280346275036721483349890283734562997453162295812514554843187025600406123086871845935705885423448082566500883583261233671214905991042886018049080003632410485801621453294172859944454727260143028850693459545380383871384743723979116050820009135538111029806791354061529803426349022636806759226133343411943740805160996777846727915758325726826233629218235034137554546259203499166132514186589427701750799300341708880818136321625010484860044798291334901322782426060704351251589123921425949054518868566186378326139856733597198227036730621370000076937417004497478671504420151401908216510458894828659751880235250608289372268714584017876873409575046890316457264221148596192997692207044105743725655505068373625523127188802794239530464182354159120415221155841310221233292750195932982781860471247017312878234371382372925305391440148324788751342235574983836462656477890547984032724349158310748518842525781474658715161384302882027702311377179992903853594306155257677815101032790951196955903378551331995631086649160781581367908510961755665476205898461596045159357962036765183549725861803977960035181392886744807575547239444024190684804454220031086573284472823441730987964143423001655087672141658916481320822201723410784666759902618305229403943584726775599949299869478918468202260422441789122390627752723285627365566724725100578530669760847197421107656297906990306065369779037734221701852087967026311459358632895252848309351168584666017311143260491719976223279758095447490069090678327047764261523630809234147038867216015374284561839343473722805889813347985221715860882265233512192671572572479474911989607238052867292149964583267738033113189406685551230774385730394947942533432746641852145302433777712400518712336728351119901278163633337792240870404322553196904771270404727644439914001505677510609806446327908612900817751735605217765500181403640558953201592278467254519426553439073753103039444111580397122590869244489152321283873160808146898180580743483475387431867169254163413998423642867438769815321940957989399426831608089756645714941461568016716354551808
        // 11249170448

        LargeInt expected = new LargeInt("11249170448");
        LargeInt modP = a.modPow(e, m);        // modPow() and pow().mod() should give same
        LargeInt check = a.pow(923).mod(m);    // results, but modPow() is more efficient and
                                               // can handle much larger exponents.

        assertEquals("modPow(a,e,m)", expected, modP);
        assertEquals("(a**e)%m", check, modP);
    }

    @Test
    public void can_multiply_two_large_values(){
        LargeInt a = new LargeInt("97675373742387693753");
        LargeInt b = new LargeInt("2451854477532");
        LargeInt na = new LargeInt("-97675373742387693753");

        LargeInt expected = new LargeInt("239485802454884810428948235257596");

        assertEquals("a*b", expected, a.multiply(b));
        assertEquals("a* (-1)", na, a.multiply(LargeInt.NEG_ONE));
    }

    @Test
    public void can_multiply_a_large_value_by_an_int(){
        LargeInt a = new LargeInt("97675373742387693753");
        LargeInt na = new LargeInt("-97675373742387693753");

        LargeInt expected = new LargeInt("99824231964720223015566");

        assertEquals("a * 1022", expected, a.multiply(1022));
        assertEquals("a* (-1)", na, a.multiply(-1));
    }

    @Test
    public void can_subtract_by_a_large_value(){
        LargeInt a = new LargeInt("724675358467671744377633");
        LargeInt b = new LargeInt("240972679096916748456940");
        LargeInt c = new LargeInt("859659751656285302520311");

        assertEquals("a - b", "483702679370754995920693", a.subtract(b).toString());
        assertEquals("b - c", "-618687072559368554063371", b.subtract(c).toString());
        assertEquals("c - b", "618687072559368554063371", c.subtract(b).toString());

        assertEquals("a - a", LargeInt.ZERO, a.subtract(a));
    }

    @Test
    public void subtracting_by_negative_works(){
        LargeInt a = new LargeInt("-1");
        LargeInt b = new LargeInt("-2");
        LargeInt c = new LargeInt("1");
        LargeInt _3 = new LargeInt("3");
        LargeInt _m3 = new LargeInt("-3");

        assertEquals("a - b", c, a.subtract(b));
        assertEquals("c - b", _3, c.subtract(b));
        assertEquals("b - a", LargeInt.NEG_ONE, b.subtract(a));
        assertEquals("b - c", _m3, b.subtract(c));
    }

    @Test
    public void can_express_large_int_as_a_floating_point_string(){
        LargeInt a = new LargeInt("455360120359063658232420111832819546343520470");
        String expectedA =        "4553601203e35";
        LargeInt b = new LargeInt("599963165776");
        String expectedB1 =       "599963165776";
        String expectedB2 =       "59996e7";
        String expectedB3 =       "5e11";
        LargeInt c = new LargeInt("-599963165776");
        String expectedC  =       "-59996e7";

        assertEquals("a // 10", expectedA, a.toFloatString(10));
        assertEquals("b // 20", expectedB1, b.toFloatString(20));
        assertEquals("b // 12", expectedB1, b.toFloatString(12));
        assertEquals("b // 5", expectedB2, b.toFloatString(5));
        assertEquals("b // 1", expectedB3, b.toFloatString(1));
        assertEquals("c // 5", expectedC, c.toFloatString(5));
    }

    @Test
    public void can_take_factorial_of_large_int(){
        LargeInt a = new LargeInt("12");
        LargeInt expectedA = new LargeInt("479001600");
        LargeInt b = new LargeInt("20");
        LargeInt expectedB = new LargeInt("2432902008176640000");

        assertEquals("a!", expectedA, a.factorial());
        assertEquals("b!", expectedB, b.factorial());
    }

    @Test
    public void can_raise_decimals_to_integer_powers(){
        LargeInt ten = new LargeInt("10");
        LargeInt res = new LargeInt("100000");

        assertEquals("10^5", res, ten.pow(5));
        assertEquals("10^(-5)", LargeInt.ZERO, res.pow(-5)); // would always be fractional
    }

    @Test
    public void can_truncate_floating_point_to_large_int(){
        LargeInt a = LargeInt.fromFloat(123456.78901);
        assertEquals("a", "123456", a.toString());

        LargeInt b = LargeInt.fromFloat(100000);
        assertEquals("b", "100000", b.toString());

        LargeInt c = LargeInt.fromFloat(0.5);
        assertEquals("c", "0", c.toString());
    }
}
