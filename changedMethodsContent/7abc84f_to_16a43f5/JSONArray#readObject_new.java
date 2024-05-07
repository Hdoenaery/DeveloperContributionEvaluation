    private void readObject(final java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
        JSONObject.SecureObjectInputStream.ensureFields();
        if (JSONObject.SecureObjectInputStream.fields != null && !JSONObject.SecureObjectInputStream.fields_error) {
            ObjectInputStream secIn = new JSONObject.SecureObjectInputStream(in);
            try {
                secIn.defaultReadObject();
                return;
            } catch (java.io.NotActiveException e) {
                // skip
            }
        }

        in.defaultReadObject();
        for (Object item : list) {
            if (item == null) {
                continue;
            }

            String typeName = item.getClass().getName();
            if (TypeUtils.getClassFromMapping(typeName) == null) {
                ParserConfig.global.checkAutoType(typeName, null);
            }
        }
    }