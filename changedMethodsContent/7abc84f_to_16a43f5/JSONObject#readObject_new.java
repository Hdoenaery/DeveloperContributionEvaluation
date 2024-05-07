    private void readObject(final java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
        SecureObjectInputStream.ensureFields();
        if (SecureObjectInputStream.fields != null && !SecureObjectInputStream.fields_error) {
            ObjectInputStream secIn = new SecureObjectInputStream(in);
            try {
                secIn.defaultReadObject();
                return;
            } catch (java.io.NotActiveException e) {
                // skip
            }
        }

        in.defaultReadObject();
        for (Entry entry : map.entrySet()) {
            final Object key = entry.getKey();
            if (key != null) {
                ParserConfig.global.checkAutoType(key.getClass());
            }

            final Object value = entry.getValue();
            if (value != null) {
                ParserConfig.global.checkAutoType(value.getClass());
            }
        }
    }