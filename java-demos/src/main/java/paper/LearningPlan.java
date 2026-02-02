package paper;

public class LearningPlan {

    public static void main(String[] args) {

        int learnDay = 3;
        dayPlan(learnDay);

//        int round = 30;
//        monthPlan(round);
    }

    /**
     * 获取某天学习计划
     *
     * @param learnDay 学习第几天
     */
    public static void dayPlan(int learnDay){
        System.out.println("-------------------------");
        System.out.println("*****第" + learnDay + "天学习计划*****");
        for (int date = 1; date <= learnDay; date++) {
            if (date == learnDay || date + 1 == learnDay || date + 3 == learnDay || date + 7 == learnDay || date + 15 == learnDay) {
                System.out.println("   背第" + date + "天" + ((date - 1) * 100) + "-" + (date * 100) + "词汇");
            }
        }
        System.out.println("-------------------------");
    }

    /**
     * 获取学习一段时间内的学习计划
     *
     * @param round 学习总时长
     */
    public static void monthPlan(int round){
        for (int learnDay = 1; learnDay <= round; learnDay++) {
            System.out.println("-------------------------");
            System.out.println("*****第" + learnDay + "天学习计划*****");
            for (int date = 1; date <= learnDay; date++) {
                if (date == learnDay || date + 1 == learnDay || date + 3 == learnDay || date + 7 == learnDay || date + 15 == learnDay) {
                    System.out.println("   背第" + date + "天" + ((date - 1) * 100) + "-" + (date * 100) + "词汇");
                }
            }
            System.out.println("-------------------------");
        }
    }
}
