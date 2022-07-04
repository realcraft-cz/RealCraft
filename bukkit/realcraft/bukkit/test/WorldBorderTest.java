package realcraft.bukkit.test;

/*
world:
    chunksPerRun: 1 (narocnejsi)
world_nether:
    chunksPerRun: 2
world_the_end:
    chunksPerRun: 2

fillTask:
  world: world
  fillDistance: 208
  chunksPerRun: 1
  tickFrequency: 1
  x: -760
  z: 760
  length: 1521
  total: 2310658
  forceLoad: false
*/
public class WorldBorderTest {

    private int x = 0;
    private int z = 0;
    private boolean isZLeg = false;
    private boolean isNeg = false;
    private int length = - 1;
    private int current = 0;
    private int reportNum = 0;

    public static void main(String[] args) {
        //System.out.println(UUID.nameUUIDFromBytes(("OfflinePlayer:"+"FluBoo").getBytes(Charsets.UTF_8)));

        (new WorldBorderTest()).run(5000);
    }

    public void run(int radius) {
        radius *= 2;
        radius /= 16;

        for (int i = 0; i < radius * radius; i++) {
            _moveNext();
        }

        System.out.println(x + "; " + z);
        System.out.println("length: " + length);
        System.out.println("reportNum: " + reportNum);
    }

    protected void _moveNext() {
        reportNum++;

        if (current < length)
            current++;
        else {    // one leg/side of the spiral down...
            current = 0;
            isZLeg ^= true;
            if (isZLeg) {    // every second leg (between X and Z legs, negative or positive), length increases
                isNeg ^= true;
                length++;
            }
        }

        if (isZLeg)
            z += (isNeg) ? - 1 : 1;
        else
            x += (isNeg) ? - 1 : 1;
    }
}
