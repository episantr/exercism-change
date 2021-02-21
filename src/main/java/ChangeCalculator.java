import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

public class ChangeCalculator {

    private final List<Integer> coins;
    private int maxCycle = 15000;

    public ChangeCalculator(List<Integer> coins) {
        this.coins = coins.stream().sorted().collect(Collectors.toList());
    }

    public List<Integer> computeMostEfficientChange(int input) {

        negativeTotalsAreNotAllowed(input);

        if (input == 0) {
            return emptyList();
        }

        changeSmallestThanAllCoinsCannotBeRepresented(input);

        coins.removeIf(coin -> coin > input);

        for(Integer coin:coins) {
            if (coin == input) {
                return singletonList(coin);
            }
        }

        updateMaxCycleForLargeAmountOfChange(input);

        List<Integer> resultList = calculateMostEfficientCoinList(input);

        if (resultList.isEmpty()) {
            throw new IllegalArgumentException("The total " + input + " cannot be represented in the given currency.");
        }

        Collections.sort(resultList);
        return resultList;
    }

    private List<Integer> calculateMostEfficientCoinList(int input) {
        SecureRandom random = new SecureRandom();
        int sum = 0;
        int randomCoin;
        List<Integer> resultList = new ArrayList<>();
        List<Integer> resultListTemp = new ArrayList<>();

        for (int i = 0; i < maxCycle; i++) {
            randomCoin = coins.get(random.nextInt(coins.size()));
            resultListTemp.add(randomCoin);
            sum += randomCoin;
            if (sum == input) {
                if (resultList.isEmpty() || resultListTemp.size() < resultList.size()) {
                    resultList = new ArrayList<>(resultListTemp);
                    resultListTemp = new ArrayList<>();
                    sum = 0;
                }
            } else if (sum > input) {
                resultListTemp = new ArrayList<>();
                sum = 0;
            }
        }
        return resultList;
    }

    private void updateMaxCycleForLargeAmountOfChange(int input) {
        if (input > 100) {
            maxCycle = maxCycle * maxCycle;
        }
    }

    private void changeSmallestThanAllCoinsCannotBeRepresented(int input) {
        boolean smallest = true;
        for (int i = 0; i < coins.size() && smallest; i++) {
            smallest = smallest && input < coins.get(i);
        }

        if (smallest) {
            throw new IllegalArgumentException("The total " + input + " cannot be represented in the given currency.");
        }
    }

    private void negativeTotalsAreNotAllowed(int input) {
        if (input < 0) {
            throw new IllegalArgumentException("Negative totals are not allowed.");
        }
    }
}
