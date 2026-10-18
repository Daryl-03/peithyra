"use client";

import { zodResolver } from "@hookform/resolvers/zod";
import { Controller, useForm } from "react-hook-form";
import type z from "zod";
import {
    Field,
    FieldError,
    FieldGroup,
    FieldLabel,
} from "@/components/ui/field";
import { Input } from "@/components/ui/input";
import {
    createDebateSchema,
    defaultCreateDebateValues,
} from "../models/create-debate-schema";

export function NewDebateForm() {
    const form = useForm<z.infer<typeof createDebateSchema>>({
        resolver: zodResolver(createDebateSchema),
        defaultValues: defaultCreateDebateValues,
        mode: "onBlur",
    });

    return (
        <form
            className="space-y-4"
            onSubmit={form.handleSubmit((data) => {
                console.log(data);
            })}
        >
            <div className="space-y-2">
                <FieldGroup>
                    <Controller
                        name="proposition"
                        control={form.control}
                        render={({ field, fieldState }) => (
                            <Field data-invalid={fieldState.invalid}>
                                <FieldLabel htmlFor="proposition">
                                    Proposition
                                </FieldLabel>
                                <Input
                                    {...field}
                                    id="proposition"
                                    aria-invalid={fieldState.invalid}
                                    aria-describedby={
                                        fieldState.invalid
                                            ? "proposition-error"
                                            : undefined
                                    }
                                    readOnly={false}
                                    placeholder="ex. La France devrait interdire les voitures thermiques."
                                    autoComplete="off"
                                    required
                                />
                                {fieldState.invalid && (
                                    <FieldError
                                        id="proposition-error"
                                        errors={[fieldState.error]}
                                    />
                                )}
                            </Field>
                        )}
                    />
                </FieldGroup>

            </div>
        </form>
    );
}
