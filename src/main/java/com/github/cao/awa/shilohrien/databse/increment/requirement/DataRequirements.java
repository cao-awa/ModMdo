package com.github.cao.awa.shilohrien.databse.increment.requirement;

import it.unimi.dsi.fastutil.objects.*;

public class DataRequirements {
    private final ObjectArraySet<DataRequirement> requirements = new ObjectArraySet<>();

    public DataRequirements() {

    }

    public DataRequirements add(DataRequirement requirement) {
        requirements.add(requirement);
        return this;
    }

    public ObjectArrayList<String> ensureSatisfy(Object t) {
        ObjectArrayList<String> reasons = new ObjectArrayList<>();
        requirements.stream().filter(requirement -> ! requirement.satisfy(t)).forEach(requirement -> reasons.add(requirement.name() + (requirement.hasMessage() ? "<" + requirement.getMessage() + ">" : "")));
        return reasons;
    }

    public boolean satisfy(Object t) {
        return requirements.stream().allMatch(requirement -> requirement.satisfy(t));
    }

    public String sign() {
        StringBuilder builder = new StringBuilder();
        requirements.stream().forEach(r -> {
            builder.append(r.name());
        });
        return builder.toString();
    }
}
