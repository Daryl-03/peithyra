"use client";

import { MoveRight } from "lucide-react";
import { useState } from "react";
import { Button } from "@/components/ui/button";
import {
    Dialog,
    DialogContent,
    DialogHeader,
    DialogTitle,
    DialogTrigger,
} from "@/components/ui/dialog";
import { NewDebateForm } from "./new-debate-form";

export function NewDebateDialog() {
    const [open, setOpen] = useState(false);

    return (
        <Dialog open={open} onOpenChange={setOpen}>
            <DialogTrigger
                render={
                    <Button variant="default" size="lg">
                        Lancer une proposition
                        <MoveRight />
                    </Button>
                }
            />
            <DialogContent>
                <DialogHeader>
                    <DialogTitle>New debate</DialogTitle>
                </DialogHeader>
                <NewDebateForm onSuccess={() => setOpen(false)} />
            </DialogContent>
        </Dialog>
    );
}
