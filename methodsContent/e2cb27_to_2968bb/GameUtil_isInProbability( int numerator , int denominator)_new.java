    public static boolean isInProbability(int numerator, int denominator) throws Exception {
        // ���ӷ�ĸ��С��0
        if (numerator <= 0 || denominator <= 0) {
            throw new Exception("�����˷Ƿ��Ĳ���");
        }
        //���Ӵ��ڷ�ĸ��һ������
        if (numerator >= denominator) {
            return true;
        }

        return getRandomNumber(1, denominator + 1) <= numerator;
    }