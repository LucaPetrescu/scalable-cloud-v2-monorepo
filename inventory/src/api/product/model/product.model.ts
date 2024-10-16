import { Prop, Schema, SchemaFactory } from '@nestjs/mongoose';

@Schema()
export class Product {
  @Prop({ unique: true })
  productId: string;

  @Prop({ unique: true })
  productName: string;

  @Prop({ unique: true })
  barCode: string;

  @Prop()
  type: string;

  @Prop()
  quantity: number;
}

export type ProductDocument = Product & Document;

export const ProductSchema = SchemaFactory.createForClass(Product);
