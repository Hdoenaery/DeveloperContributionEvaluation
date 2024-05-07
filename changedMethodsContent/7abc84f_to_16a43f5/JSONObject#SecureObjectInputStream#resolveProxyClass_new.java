        public SecureObjectInputStream(ObjectInputStream in) throws IOException {
            super(in);
            try {
                for (int i = 0; i < fields.length; i++) {
                    final Field field = fields[i];
                    final Object value = field.get(in);
                    field.set(this, value);
                }
            } catch (IllegalAccessException e) {
                fields_error = true;
            }
        }