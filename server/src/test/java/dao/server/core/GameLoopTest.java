package dao.server.core;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class GameLoopTest {

    @Test
    void step_runsQueuedCommands_thenAdvancesTick() {
        GameLoop loop = new GameLoop();

        loop.submit(world -> world.addPlayer(new Player(1, "An")));
        assertEquals(0, loop.world().playerCount(), "chưa step thì Command chưa chạy");

        loop.step();

        assertEquals(1, loop.world().playerCount());
        assertEquals(1, loop.world().tick());
    }

    @Test
    void systems_runEveryTick_inRegistrationOrder() {
        GameLoop loop = new GameLoop();
        List<String> order = new ArrayList<>();
        loop.addSystem(world -> order.add("A"));
        loop.addSystem(world -> order.add("B"));

        loop.step();
        loop.step();

        assertEquals(List.of("A", "B", "A", "B"), order);
    }

    @Test
    void sixtySeconds_ofTicks_takeMilliseconds() {
        GameLoop loop = new GameLoop();

        for (int i = 0; i < 1200; i++) { // 60 giây * 20 tick/giây
            loop.step();
        }

        assertEquals(1200, loop.world().tick());
    }

    @Test
    void commandsSubmittedFromManyThreads_allRunExactlyOnce() throws InterruptedException {
        GameLoop loop = new GameLoop();
        int threads = 4;
        int perThread = 500;

        List<Thread> workers = new ArrayList<>();
        for (int t = 0; t < threads; t++) {
            int base = t * perThread; // mỗi thread dùng một dải id riêng
            Thread worker =
                    new Thread(
                            () -> {
                                for (int i = 0; i < perThread; i++) {
                                    int id = base + i;
                                    loop.submit(world -> world.addPlayer(new Player(id, "p" + id)));
                                }
                            });
            workers.add(worker);
            worker.start();
        }
        for (Thread worker : workers) {
            worker.join();
        }

        loop.step();

        assertEquals(threads * perThread, loop.world().playerCount());
    }
}