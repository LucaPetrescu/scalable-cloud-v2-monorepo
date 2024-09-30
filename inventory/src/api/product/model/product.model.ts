import { Prop, Schema, SchemaFactory } from '@nestjs/mongoose';

@Schema()
export class Product {
  @Prop()
  productName: string;

  @Prop()
  barCode: string;

  @Prop()
  type: string;

  @Prop()
  quantity: number;
}

export type ProductDocument = Product & Document;

export const ProductSchema = SchemaFactory.createForClass(Product);
