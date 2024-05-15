    public Class<?> checkAutoType(Class type) {
        if (get(type) != null) {
            return type;
        }

        return checkAutoType(type.getName(), null, JSON.DEFAULT_PARSER_FEATURE);
    }