package world.bentobox.bentobox.commands;

import java.util.List;

import world.bentobox.bentobox.BentoBox;
import world.bentobox.bentobox.api.commands.CompositeCommand;
import world.bentobox.bentobox.api.user.User;

/**
 * Displays generic information about BentoBox such as version and license.
 * @author tastybento
 */
public class BentoBoxAboutCommand extends CompositeCommand {

    /**
     * About
     * @param parent parent CompositeCommand
     */
    public BentoBoxAboutCommand(CompositeCommand parent) {
        super(parent, "about");
    }

    @Override
    public void setup() {
        setPermission("bentobox.about");
    }

    @Override
    public boolean execute(User user, String label, List<String> args) {
        user.sendRawMessage("关于 " + BentoBox.getInstance().getDescription().getName() + " v" + BentoBox.getInstance().getDescription().getVersion() + ":");
        user.sendRawMessage("Copyright (c) 2017 - 2020 Tastybento, Poslovitch");
        user.sendRawMessage("由 Jeansou 提供汉化");
        user.sendRawMessage("访问 https://www.eclipse.org/legal/epl-2.0/ 以查看详细版权信息.");
        return true;
    }
}
